package delivery.api;

import http.payment.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod paymentMethod
) {
}
