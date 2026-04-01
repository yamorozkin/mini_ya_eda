package http.order.model.dto;

import http.order.model.status.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponseDto(
        Long id,
        String street,
        Long houseNumber,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemResponseDto> items
) {
}