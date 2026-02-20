package delivery.domain;

import delivery.api.OrderPaymentRequest;
import delivery.domain.db.OrderEntity;
import delivery.domain.db.OrderEntityMapper;
import delivery.domain.db.OrderItemEntity;
import delivery.domain.db.OrderJpaRepository;
import delivery.external.PaymentHttpClient;
import http.order.CreateOrderRequestDto;
import http.order.OrderStatus;
import http.payment.CreatePaymentRequestDto;
import http.payment.CreatePaymentResponseDto;
import http.payment.PaymentStatus;
import kafka.DeliveryAssignedEvent;
import kafka.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository repository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    public OrderEntity create(CreateOrderRequestDto request) {

        var entity = orderEntityMapper.toEntity(request);
        calculatePricingForOrder(entity);
        entity.setOrderStatus(http.order.OrderStatus.PENDING_PAYMENT);
        return repository.save(entity);
    }

    private void calculatePricingForOrder(OrderEntity entity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(OrderItemEntity item : entity.getItems()){
            var randomPrice = ThreadLocalRandom.current().nextDouble(100,5000);
            item.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));
            totalPrice = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())).add(totalPrice);
        }
        entity.setTotalAmount(totalPrice);
    }


    public OrderEntity getOrderOrThrow(Long id) {
        var orderEntityOptional = repository.findById(id);
        return orderEntityOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    public OrderEntity processPayment(Long id, OrderPaymentRequest request) {
        var entity = getOrderOrThrow(id);
        if(!entity.getOrderStatus().equals(http.order.OrderStatus.PENDING_PAYMENT)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        var response = paymentHttpClient.createPayment(CreatePaymentRequestDto.builder()
                .orderId(id)
                .paymentMethod(request.paymentMethod())
                .amount(entity.getTotalAmount())
                .build());
        var status = response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCCEEDED)
                ? OrderStatus.PAYMENT_FAILED
                : OrderStatus.PAID;
        entity.setOrderStatus(status);
        sendOrderPaidEvent(entity, response);

        return repository.save(entity);
    }

    private void sendOrderPaidEvent(OrderEntity entity, CreatePaymentResponseDto response) {
        kafkaTemplate.send(
                orderPaidTopic,
                entity.getId(),
                OrderPaidEvent.builder()
                        .orderId(entity.getId())
                        .amount(entity.getTotalAmount())
                        .paymentMethod(response.paymentMethod())
                        .paymentId(response.paymentId())
                        .build()
        );
    }


    public void processDeliveryAssigned(DeliveryAssignedEvent event) {
        var order = getOrderOrThrow(event.orderId());
        if(!order.getOrderStatus().equals(http.order.OrderStatus.PAID)){
            processIncorrectState(order);
            return;
        }
        order.setOrderStatus(http.order.OrderStatus.DELIVERY_ASSIGNED);
        order.setCourierName(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        repository.save(order);
    }

    private void processIncorrectState(OrderEntity order) {
        if(order.getOrderStatus().equals(http.order.OrderStatus.DELIVERY_ASSIGNED)){
            return;
        }else if (!order.getOrderStatus().equals(http.order.OrderStatus.PAID)){
            return;
        }
    }
}
