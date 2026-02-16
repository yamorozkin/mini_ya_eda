package http.order;

import delivery.domain.OrderEntity;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link OrderEntity}
 */
public record OrderDto(Long id, Long customerId, String address, BigDecimal totalAmount, String courierName,
                       Integer etaMinutes, OrderStatus orderStatus, Set<OrderItemEntityDto> orderItemEntities) {
}