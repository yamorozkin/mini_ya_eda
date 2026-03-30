package delivery.model.db;

import java.util.Optional;

public interface PaymentEntityRepository extends org.springframework.data.jpa.repository.JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByOrderId(Long orderId);
}