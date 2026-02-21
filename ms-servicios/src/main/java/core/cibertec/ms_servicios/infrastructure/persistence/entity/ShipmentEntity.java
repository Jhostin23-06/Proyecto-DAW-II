package core.cibertec.ms_servicios.infrastructure.persistence.entity;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipments")
public class ShipmentEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipment_seq")
    @SequenceGenerator(name = "shipment_seq", sequenceName = "shipment_id_seq", allocationSize = 1)
    @Column(name = "shipment_id")
    private Long shipmentId;

    @Column(name = "category_id")
    private Long categoryId;
    private String description;
    private Double price;
    private Double weight;
    private Double volume;
    private String origin;
    private String destination;
    private Long clientId;
    private String transportId;
    private String orderNumber;
    private String status; // legacy column for backward compatibility

    private LocalDateTime atDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private ShipmentCategoryEntity categoryRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private ShipmentStatusEntity statusRef;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
