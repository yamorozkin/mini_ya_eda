package delivery.repository;

import delivery.model.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeliveryEntityRepository extends JpaRepository<DeliveryEntity, Long> {
    DeliveryEntity findByOrderId(Long orderId);

    //поиск свободных курьеров
    @Query("SELECT d FROM DeliveryEntity d WHERE d.orderId = 0")
    List<DeliveryEntity> findFreeCouriers();

}