package http.order.model.dto;

public record OrderItemRequestDto(
        Long itemId,
        Integer quantity
) {
}
