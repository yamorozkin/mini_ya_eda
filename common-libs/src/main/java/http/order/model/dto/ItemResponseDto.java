package http.order.model.dto;

import java.math.BigDecimal;

public record ItemResponseDto(
        Long id,
        String name,
        BigDecimal price,
        String imageUrl
) {
}