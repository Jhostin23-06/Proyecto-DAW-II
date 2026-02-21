package core.cibertec.ms_servicios.infrastructure.persistence.repository;

import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatusEntity, Long> {
    Optional<ShipmentStatusEntity> findByStatusNameIgnoreCase(String statusName);
}
