package core.cibertec.ms_servicios.domain.bean;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "categoryName is required")
    private String categoryName;
}
