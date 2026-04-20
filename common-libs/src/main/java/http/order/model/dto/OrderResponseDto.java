package http.order.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import http.order.model.status.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

//чтобы спрятать null поля
@JsonInclude(JsonInclude.Include.NON_NULL)
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