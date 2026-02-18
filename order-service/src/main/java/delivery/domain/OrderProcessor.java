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
import http.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return repository.save(entity);
    }


}
