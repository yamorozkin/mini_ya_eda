package delivery.model;

import http.payment.model.status.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod paymentMethod
) {
}
