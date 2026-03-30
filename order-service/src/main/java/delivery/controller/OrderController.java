package delivery.controller;

import delivery.mapper.OrderEntityMapper;
import delivery.model.OrderPaymentRequest;
import delivery.service.OrderService;
import http.order.model.dto.CreateOrderRequestDto;
import http.order.model.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

//В качестве ответа используется одна dto - OrderDto, запросы представляют разные dto.

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderEntityMapper orderEntityMapper;
    private final Logger log =  LoggerFactory.getLogger(OrderController.class);

    /*
    Создание заказа.

    На вход:
    customerId, address,
    Set<OrderItemRequestDto> items

    На выход:
    id, customerId, address,
    totalAmount, courierName,
    etaMinutes, OrderStatus,
    Set<OrderItemEntityDto> orderItemEntities

    */

    @PostMapping
    public OrderDto create(
            @RequestBody CreateOrderRequestDto request
    ) {
        var saved = orderService.create(request);
        log.info("Created order: {}", saved);
        return orderEntityMapper.toOrderDto(saved);
    }

    /*
    Оплата заказа.

    На вход:
    id,
    PaymentMethod

    На выход:
    id, customerId, address,
    totalAmount, courierName,
    etaMinutes, OrderStatus,
    Set<OrderItemEntityDto> orderItemEntities
    */

    @PostMapping("/{id}/pay")
    public OrderDto payOrder(@PathVariable Long id,
            @RequestBody OrderPaymentRequest request) {
        var entity = orderService.processPayment(id, request);
        log.info("Payment order: {}", entity);
        return orderEntityMapper.toOrderDto(entity);
    }

    /*
    Получение данных о заказе.

    На вход:
    id,

    На выход:
    id, customerId, address,
    totalAmount, courierName,
    etaMinutes, OrderStatus,
    Set<OrderItemEntityDto> orderItemEntities
    */

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        var found = orderService.getOrderOrThrow(id);
        log.info("Found order: {}", found);
        return orderEntityMapper.toOrderDto(found);
    }
}
