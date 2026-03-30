package http.payment.model.dto;


import http.payment.model.status.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRequestDto(
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount

) {
}
