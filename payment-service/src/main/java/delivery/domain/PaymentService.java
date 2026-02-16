package delivery.domain;

import delivery.domain.db.PaymentEntityMapper;
import delivery.domain.db.PaymentEntityRepository;
import http.payment.CreatePaymentRequestDto;
import http.payment.CreatePaymentResponseDto;
import http.payment.PaymentMethod;
import http.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentEntityMapper mapper;

    private final PaymentEntityRepository repository;


    public CreatePaymentResponseDto makePayment(CreatePaymentRequestDto request) {

        var found = repository.findByOrderId(request.orderId());

        if(found.isPresent()) {
            return mapper.toResponseDto(found.get());
        }

        var entity = mapper.toEntity(request);

        var status = request.paymentMethod().equals(PaymentMethod.QR) ?
                PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCCEEDED;

        entity.setPaymentStatus(status);
        var savedEntity = repository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }
}
