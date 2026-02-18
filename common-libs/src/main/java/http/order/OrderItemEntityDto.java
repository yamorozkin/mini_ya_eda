package http.order;


import java.math.BigDecimal;


public record OrderItemEntityDto(Long id, Integer quantity, BigDecimal priceAtPurchase, Long itemId) {
}