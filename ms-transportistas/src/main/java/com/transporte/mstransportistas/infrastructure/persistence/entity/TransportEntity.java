package com.transporte.mstransportistas.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transports", indexes = {
        @Index(name = "idx_transport_status", columnList = "transportStatus"),
        @Index(name = "idx_transport_user", columnList = "transportUserId"),
        @Index(name = "idx_license_plate", columnList = "transportLicensePlate", unique = true)
})
public class TransportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID, generator = "transport_seq")
    @SequenceGenerator(name = "transport_seq", sequenceName = "transport_id_seq", allocationSize = 1)
    @Column(name = "transport_id")
    private String transportId;

    @Column(name = "transport_user_id")
    private String transportUserId;

    @Column(name = "transport_type", nullable = false, length = 50)
    private String transportType;

    @Column(name = "transport_capacity", nullable = false)
    private Double transportCapacity;

    @Column(name = "transport_status", nullable = false, length = 20)
    private String transportStatus;

    @Column(name = "transport_location", length = 200)
    private String transportLocation;

    @Column(name = "transport_driver", nullable = false, length = 100)
    private String transportDriver;

    @Column(name = "transport_license_plate", nullable = false, unique = true, length = 20)
    private String transportLicensePlate;

    @Column(name = "transport_company", nullable = false, length = 100)
    private String transportCompany;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

}
