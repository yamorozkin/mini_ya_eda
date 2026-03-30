package delivery.service;

import delivery.mapper.PaymentEntityMapper;
import delivery.repository.PaymentEntityRepository;
import http.payment.model.dto.CreatePaymentRequestDto;
import http.payment.model.dto.CreatePaymentResponseDto;
import http.payment.model.status.PaymentMethod;
import http.payment.model.status.PaymentStatus;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentEntityMapper mapper;
    private final PaymentEntityRepository repository;
    private final Logger log =  LoggerFactory.getLogger(PaymentService.class);

    //Создание заказа.

    public CreatePaymentResponseDto makePayment(CreatePaymentRequestDto request) {
        var found = repository.findByOrderId(request.orderId());

        if(found.isPresent()) {
            return mapper.toResponseDto(found.get());
        }
        var entity = mapper.toEntity(request);

        //Заглушка для статуса оплаты: шанс успеха - 90%, провала - 10%.

        int successChance = 90;
        int randomValue = ThreadLocalRandom.current().nextInt(0, 100);
        var status = (randomValue < successChance)
                ? PaymentStatus.PAYMENT_SUCCEEDED
                : PaymentStatus.PAYMENT_FAILED;

        entity.setPaymentStatus(status);
        var savedEntity = repository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }
}
