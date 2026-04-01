package kafka;

import http.payment.model.status.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderPaidEvent(
        Long orderId,
        Long paymentId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String street,
        Long houseNumber
) {
}
