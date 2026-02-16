package http.order;

import delivery.domain.OrderItemEntity;

import java.math.BigDecimal;

/**
 * DTO for {@link OrderItemEntity}
 */
public record OrderItemEntityDto(Long id, Integer quantity, BigDecimal priceAtPurchase, Long itemId) {
}