package http.order.model.dto;


import java.util.Set;

public record CreateOrderRequestDto (
         Long customerId,
         String street,
         Long houseNumber,
         Set<OrderItemRequestDto> items
){
}
