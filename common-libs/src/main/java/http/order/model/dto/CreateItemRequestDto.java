package http.order.model.dto;

import java.math.BigDecimal;

public record CreateItemRequestDto(
        String name,
        BigDecimal price,
        String imageUrl
) {
}