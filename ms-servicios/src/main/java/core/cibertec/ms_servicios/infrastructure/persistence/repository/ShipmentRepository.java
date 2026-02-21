package core.cibertec.ms_servicios.infrastructure.persistence.repository;

import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<ShipmentEntity, Long> {
    List<ShipmentEntity> findByOrderNumber(String orderNumber);
}
