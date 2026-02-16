package delivery.api;
import delivery.domain.PaymentService;
import http.payment.CreatePaymentRequestDto;
import http.payment.CreatePaymentResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto request) {



        return paymentService.makePayment(request);
    }


}
