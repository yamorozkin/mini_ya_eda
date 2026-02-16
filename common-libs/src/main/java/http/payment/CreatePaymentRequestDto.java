package http.payment;


import java.math.BigDecimal;

public record CreatePaymentRequestDto(
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount

) {
}
