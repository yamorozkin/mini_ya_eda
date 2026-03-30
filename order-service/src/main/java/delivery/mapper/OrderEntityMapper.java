package delivery.mapper;

import delivery.model.entity.OrderEntity;
import delivery.model.entity.OrderItemEntity;
import http.order.model.dto.CreateOrderRequestDto;
import http.order.model.dto.OrderDto;
import http.order.model.dto.OrderItemEntityDto;
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