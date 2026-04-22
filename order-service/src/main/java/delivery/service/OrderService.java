//нужно дописать логику delivered статуса

package delivery.service;

import delivery.model.OrderPaymentRequest;
import delivery.model.entity.OrderEntity;
import delivery.mapper.OrderEntityMapper;
import delivery.model.entity.OrderItemEntity;
import delivery.repository.ItemJpaRepository;
import delivery.repository.OrderJpaRepository;
import delivery.external.PaymentHttpClient;
import delivery.external.UserHttpClient;
import http.order.model.dto.CreateOrderRequestDto;
import http.order.model.status.OrderStatus;
import http.payment.model.dto.CreatePaymentRequestDto;
import http.payment.model.dto.CreatePaymentResponseDto;
import http.payment.model.status.PaymentStatus;
import kafka.DeliveryAssignedEvent;
import kafka.DeliveryFinishedEvent;
import kafka.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderJpaRepository repository;
    private final ItemJpaRepository itemJpaRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;
    private final UserHttpClient userHttpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;
    private final Logger log =  LoggerFactory.getLogger(OrderService.class);

    //Кафка используется для взаимодействия delivery service <-> order service, асинхронно.

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    //Создание заказа.

    public OrderEntity create(CreateOrderRequestDto request, String authorizationHeader) {
        Long customerId = resolveCurrentUserId(authorizationHeader);

        var entity = orderEntityMapper.toOrderEntity(request);
        entity.setCustomerId(customerId);
        enrichOrderItemsFromCatalog(entity);
        calculatePricingForOrder(entity);
        entity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        OrderEntity savedEntity = repository.save(entity);
        return savedEntity;
    }

    private Long resolveCurrentUserId(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is required");
        }
        try {
            var user = userHttpClient.getMyProfile(authorizationHeader);
            if (user == null || user.id() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot resolve user from token");
            }
            return user.id();
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Cannot reach user-service to resolve current user"
            );
        }
    }

    //Заполнение данных из таблицы товаров

    private void enrichOrderItemsFromCatalog(OrderEntity entity) {
        for(OrderItemEntity orderItem : entity.getItems()){
            var catalogItem = itemJpaRepository.findById(orderItem.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            orderItem.setName(catalogItem.getName());
            orderItem.setPriceAtPurchase(catalogItem.getPrice());
            orderItem.setOrder(entity);
        }
    }

    //Вычисление цены заказа

    private void calculatePricingForOrder(OrderEntity entity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(OrderItemEntity orderItem : entity.getItems()){
            BigDecimal lineTotal = orderItem.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalPrice = totalPrice.add(lineTotal);
        }
        entity.setTotalAmount(totalPrice);
    }

    //Получение заказа, проверяется только наличие в бд сущности с указанным айди.

    public OrderEntity getOrderOrThrow(Long id) {
        var orderEntityOptional = repository.findById(id);
        return orderEntityOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Entity with id `%s` not found".formatted(id)));
    }

    public OrderEntity getOrderOrThrowForCurrentUser(Long id, String authorizationHeader) {
        Long customerId = resolveCurrentUserId(authorizationHeader);
        OrderEntity entity = getOrderOrThrow(id);
        assertOrderOwnership(entity, customerId);
        return entity;
    }

    //Оплата order service <-> payment service, синхронно, через клиент.

    public OrderEntity processPayment(Long id, OrderPaymentRequest request, String authorizationHeader) {
        Long customerId = resolveCurrentUserId(authorizationHeader);
        var entity = getOrderOrThrow(id);
        assertOrderOwnership(entity, customerId);
        if(!entity.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        var response = paymentHttpClient.createPayment(CreatePaymentRequestDto.builder()
                .orderId(id)
                .paymentMethod(request.paymentMethod())
                .amount(entity.getTotalAmount())
                .build());

        var status = response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCCEEDED)
                ? OrderStatus.PAID
                : OrderStatus.PAYMENT_FAILED;

        entity.setOrderStatus(status);
        var savedEntity = repository.save(entity);

        //Отправка в кафку события исключительно успешной оплаты.

        if (status == OrderStatus.PAID){
            sendOrderPaidEvent(entity, response);
        }
        else{
            log.info("Payment failed for order with id `{}`", id);
            log.info("Event won't send");
        }

        return savedEntity;
    }

    private void assertOrderOwnership(OrderEntity entity, Long customerId) {
        if (!customerId.equals(entity.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this order");
        }
    }

    //Отправка события в кафку (для сервиса доставки).

    private void sendOrderPaidEvent(OrderEntity entity, CreatePaymentResponseDto response) {
        kafkaTemplate.send(
                orderPaidTopic,
                entity.getId(),
                OrderPaidEvent.builder()
                        .orderId(entity.getId())
                        .amount(entity.getTotalAmount())
                        .paymentMethod(response.paymentMethod())
                        .paymentId(response.paymentId())
                        .street(entity.getStreet())
                        .houseNumber(entity.getHouseNumber())
                        .build()
        );
    }

    //Получение информации о доставке.

    public void processDeliveryAssigned(DeliveryAssignedEvent event) {
        var order = getOrderOrThrow(event.orderId());

        //Для предотвращения дублирования (доставщик уже был назначен - ничего не делаем).

//        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
//            log.info("Delivery already done to order with id `{}`", event.orderId());
//            return;
//        }

        order.setOrderStatus(OrderStatus.DELIVERY_ASSIGNED);
        order.setCourierName(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        repository.save(order);
    }

    public void processDeliveryFinished(DeliveryFinishedEvent event) {
        var order = getOrderOrThrow(event.orderId());

        //Для предотвращения дублирования (доставщик уже был назначен - ничего не делаем).

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            log.info("Delivery already delivered to order with id `{}`", event.orderId());
            return;
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setCourierName(event.courierName());
        order.setEtaMinutes(null);
        log.info("delivery ={} is finished", event);
        repository.save(order);
    }
}
