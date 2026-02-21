package core.cibertec.ms_servicios.infrastructure.persistence.repository;

import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentCategoryRepository extends JpaRepository<ShipmentCategoryEntity, Long> {
    Optional<ShipmentCategoryEntity> findByCategoryNameIgnoreCase(String categoryName);
}
