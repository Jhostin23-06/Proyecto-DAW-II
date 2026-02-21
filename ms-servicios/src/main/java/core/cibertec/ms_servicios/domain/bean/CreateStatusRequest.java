package core.cibertec.ms_servicios.domain.bean;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "statusName is required")
    private String statusName;
}
