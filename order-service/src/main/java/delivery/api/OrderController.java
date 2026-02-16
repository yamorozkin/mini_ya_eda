package delivery.api;

import delivery.domain.db.OrderEntityMapper;
import delivery.domain.OrderProcessor;
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
    public OrderDto create(@RequestBody http.order.CreateOrderRequestDto request) {
        var saved = orderProcessor.create(request);
        return orderEntityMapper.toOrderDto(saved);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        var found = orderProcessor.getOrderOrThrow(id);
        return orderEntityMapper.toOrderDto(found);
    }
}
