package core.cibertec.ms_servicios.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
