package core.cibertec.ms_servicios.domain.bean;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "categoryId is required")
    private Long categoryId;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "price is required")
    @Positive(message = "price must be positive")
    private Double price;

    @NotNull(message = "weight is required")
    @Positive(message = "weight must be positive")
    private Double weight;

    @NotNull(message = "volume is required")
    @Positive(message = "volume must be positive")
    private Double volume;

    @NotBlank(message = "origin is required")
    private String origin;

    @NotBlank(message = "destination is required")
    private String destination;

    @NotNull(message = "clientId is required")
    @Positive(message = "clientId must be positive")
    private Long clientId;

    private Long transportId;

    @NotBlank(message = "orderNumber is required")
    private String orderNumber;

    private LocalDateTime atDate;

}
