package http.order.model.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long itemId,
        String name,
        BigDecimal priceAtPurchase,
        Integer quantity
) {
}