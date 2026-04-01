package http.order.model.dto;


import http.order.model.status.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;


public record OrderDto(
        Long id,
        Long customerId,
        String street,
        Long houseNumber,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemEntityDto> orderItemEntities
) {
}