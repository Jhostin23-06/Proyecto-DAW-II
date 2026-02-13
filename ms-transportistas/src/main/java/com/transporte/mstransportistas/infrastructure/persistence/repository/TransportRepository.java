package com.transporte.mstransportistas.infrastructure.persistence.repository;

import com.transporte.mstransportistas.infrastructure.persistence.entity.TransportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportRepository extends JpaRepository<TransportEntity, Long> {

    List<TransportEntity> findByTransportStatus(String status);

    List<TransportEntity> findByTransportStatusAndActiveTrue(String status);

    List<TransportEntity> findByTransportUserId(Long userId);

    Optional<TransportEntity> findByTransportLicensePlate(String licensePlate);

    boolean existsByTransportLicensePlate(String licensePlate);

    @Query("SELECT t FROM TransportEntity t WHERE t.transportStatus = 'AVAILABLE' AND t.active = true")
    List<TransportEntity> findAvailableTransports();

    @Query("SELECT t FROM TransportEntity t WHERE t.transportStatus = 'AVAILABLE' " +
            "AND t.active = true AND t.transportCapacity >= :minCapacity")
    List<TransportEntity> findAvailableTransportsWithCapacity(@Param("minCapacity") Double minCapacity);

    List<TransportEntity> findByActiveTrue();

    @Query("SELECT t FROM TransportEntity t WHERE t.active = true " +
            "AND (:status IS NULL OR t.transportStatus = :status) " +
            "AND (:userId IS NULL OR t.transportUserId = :userId)")
    List<TransportEntity> findByFilters(@Param("status") String status,
                                        @Param("userId") Long userId);

}
