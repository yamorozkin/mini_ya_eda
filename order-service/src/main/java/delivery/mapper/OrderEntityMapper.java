package delivery.mapper;

import delivery.model.entity.OrderEntity;
import delivery.model.entity.OrderItemEntity;
import http.order.model.dto.CreateOrderRequestDto;
import http.order.model.dto.OrderItemRequestDto;
import http.order.model.dto.OrderResponseDto;
import http.order.model.dto.OrderItemResponseDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)

public interface OrderEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "courierName", ignore = true)
    @Mapping(target = "etaMinutes", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)

    OrderEntity toOrderEntity(CreateOrderRequestDto requestDto);
    OrderResponseDto toOrderResponseDto(OrderEntity orderEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "priceAtPurchase", ignore = true)

    OrderItemEntity toOrderItemEntity(OrderItemRequestDto dto);
    OrderItemResponseDto toOrderItemResponseDto(OrderItemEntity entity);
    
    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity
                .getItems()
                .forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }
    



}