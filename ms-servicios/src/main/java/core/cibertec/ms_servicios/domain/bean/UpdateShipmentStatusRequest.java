package core.cibertec.ms_servicios.domain.bean;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShipmentStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "statusId is required")
    @Positive(message = "statusId must be positive")
    @Setter(AccessLevel.NONE)
    private Long statusId;

    @JsonSetter("statusId")
    public void setStatusId(Object value) {
        if (value == null) {
            this.statusId = null;
            return;
        }

        if (value instanceof Number number) {
            this.statusId = number.longValue();
            return;
        }

        String text = value.toString().trim();
        if (text.isEmpty() || "null".equalsIgnoreCase(text) || "undefined".equalsIgnoreCase(text)) {
            this.statusId = null;
            return;
        }

        try {
            this.statusId = Long.parseLong(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("statusId must be a numeric id");
        }
    }
}
