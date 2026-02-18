package kafka;

import http.payment.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DeliveryAssignedEvent(
        Long orderId,
        String courierName,
        Integer etaMinutes
) {
}
