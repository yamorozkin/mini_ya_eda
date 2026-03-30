package delivery.service;

import delivery.mapper.PaymentEntityMapper;
import delivery.repository.PaymentEntityRepository;
import http.payment.model.dto.CreatePaymentRequestDto;
import http.payment.model.dto.CreatePaymentResponseDto;
import http.payment.model.status.PaymentMethod;
import http.payment.model.status.PaymentStatus;
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
