//Проверить работоспособность всех сервисов, есть ли null поля при выводе, если что их скрыть + те,
//что пользователь не должен видеть.

/*
надо дописать:
1) изменить структуру вынести все дто, энтити мапперы и тд. готово
2) на ордер сервисе добавить бд с адресами
3) на доставке добавить бд с курьерами у определенных адресов
4) для курьеров реализовать время доставки, добавить им всем поле со статусами, если курьер сейчас доставляет,
то не получится пока назначить нового. мб многопоток сделать
5) сделать в доставке метод который выбирает ближайшего к адресу курьера
можно сделать только одну улицу с номерами домов, будет назначаться курьер который стоит у ближайшего дома;
после того как курьер типо назначен он должен в его бд переместиться к новому адресу
6) написать user-service с SpringSecurity
7) фронт-энд
 */


package delivery.controller;

import delivery.mapper.OrderEntityMapper;
import delivery.model.OrderPaymentRequest;
import delivery.service.OrderService;
import http.order.model.dto.CreateOrderRequestDto;
import http.order.model.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

//В качестве ответа используется одна dto - OrderDto, запросы представляют разные dto.

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderEntityMapper orderEntityMapper;
    private final Logger log =  LoggerFactory.getLogger(OrderController.class);

    /*
    Создание заказа.

    На вход:
    customerId, street, houseNumber
    Set<OrderItemRequestDto> items

    На выход:
    id, customerId, address,
    totalAmount, courierName,
    etaMinutes, OrderStatus,
    Set<OrderItemEntityDto> orderItemEntities

    */

    @PostMapping
    public OrderDto create(
            @RequestBody CreateOrderRequestDto request
    ) {
        var saved = orderService.create(request);
        log.info("Created order: {}", saved);
        return orderEntityMapper.toOrderDto(saved);
    }

    /*
    Оплата заказа.

    На вход:
    id,
    PaymentMethod

    На выход:
    id, customerId, address,
    totalAmount, courierName,
    etaMinutes, OrderStatus,
    Set<OrderItemEntityDto> orderItemEntities
    */

    @PostMapping("/{id}/pay")
    public OrderDto payOrder(@PathVariable Long id,
            @RequestBody OrderPaymentRequest request) {
        var entity = orderService.processPayment(id, request);
        log.info("Payment order: {}", entity);
        return orderEntityMapper.toOrderDto(entity);
    }

    /*
    Получение данных о заказе.

    На вход:
    id,

    На выход:
    id, customerId, address,
    totalAmount, courierName,
    etaMinutes, OrderStatus,
    Set<OrderItemEntityDto> orderItemEntities
    */

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        var found = orderService.getOrderOrThrow(id);
        log.info("Found order: {}", found);
        return orderEntityMapper.toOrderDto(found);
    }
}
