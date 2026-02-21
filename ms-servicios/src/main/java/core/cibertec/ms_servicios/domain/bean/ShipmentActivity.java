package core.cibertec.ms_servicios.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentActivity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long shipmentActivityId;
    private Long shipmentId;
    private Long userId;
    private Long statusId;
    private LocalDateTime atDate;

}
