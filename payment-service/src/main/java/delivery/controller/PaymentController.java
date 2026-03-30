package delivery.controller;
import delivery.service.PaymentService;
import http.payment.model.dto.CreatePaymentRequestDto;
import http.payment.model.dto.CreatePaymentResponseDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final Logger log =  LoggerFactory.getLogger(PaymentController.class);

    /*
    Оплата заказа.

    На вход:
    orderId,
    PaymentMethod ,
    amount

    На выход:
    paymentId,
    PaymentStatus,
    orderId,
    PaymentMethod,
    amount
    */

    @PostMapping
    public CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto request) {
        log.info("Trying to make payment");
        return paymentService.makePayment(request);
    }


}
