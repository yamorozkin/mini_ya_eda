package kafka;


import lombok.Builder;

@Builder
public record DeliveryFinishedEvent(
        Long orderId,
        String courierName
) {
}
