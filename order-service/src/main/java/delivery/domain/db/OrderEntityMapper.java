package delivery.domain.db;

import http.order.CreateOrderRequestDto;
import http.order.OrderDto;
import http.order.OrderItemEntityDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {
    
    @Mapping(target = "order", ignore = true)
    OrderItemEntity toOrderItemEntity(OrderItemEntityDto dto);
    
    OrderItemEntityDto toOrderItemEntityDto(OrderItemEntity entity);
    
    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity
                .getItems()
                .forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }
    
    OrderEntity toEntity(CreateOrderRequestDto requestDto);

    @Mapping(source = "items", target = "orderItemEntities")
    OrderDto toOrderDto(OrderEntity orderEntity);
}