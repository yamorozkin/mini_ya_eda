package delivery.controller;

import delivery.mapper.OrderEntityMapper;
import delivery.model.OrderPaymentRequest;
import delivery.service.OrderService;
import http.order.model.dto.CreateOrderRequestDto;
import http.order.model.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderEntityMapper orderEntityMapper;

    @PostMapping
    public OrderDto create(
            @RequestBody CreateOrderRequestDto request
    ) {
        var saved = orderService.create(request);
        return orderEntityMapper.toOrderDto(saved);
    }

    @PostMapping("/{id}/pay")
    public OrderDto payOrder(@PathVariable Long id,
            @RequestBody OrderPaymentRequest request) {
        var entity = orderService.processPayment(id, request);
        return orderEntityMapper.toOrderDto(entity);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        var found = orderService.getOrderOrThrow(id);
        return orderEntityMapper.toOrderDto(found);
    }
}
