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

    private Long categoryId;
    private String description;
    private Double price;
    private Double weight;
    private Double volume;
    private String origin;
    private String destination;
    private Long clientId;
    private Long transportId;
    private String orderNumber;
    private String status;

    private LocalDateTime atDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
