package delivery.api;

import delivery.domain.db.OrderEntityMapper;
import delivery.domain.OrderProcessor;
import http.order.CreateOrderRequestDto;
import http.order.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderEntityMapper orderEntityMapper;

    @PostMapping
    public OrderDto create(
            @RequestBody CreateOrderRequestDto request
    ) {
        var saved = orderProcessor.create(request);
        return orderEntityMapper.toOrderDto(saved);
    }

    @PostMapping("/{id}/pay")
    public OrderDto payOrder(@PathVariable Long id,
            @RequestBody OrderPaymentRequest request) {
        var entity = orderProcessor.processPayment(id, request);
        return orderEntityMapper.toOrderDto(entity);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        var found = orderProcessor.getOrderOrThrow(id);
        return orderEntityMapper.toOrderDto(found);
    }
}
