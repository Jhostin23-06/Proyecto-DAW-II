package core.cibertec.ms_servicios.domain.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Data
@NoArgsConstructor
public class ShipmentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Categoría es requerida")
    @Setter(AccessLevel.NONE)
    private Long categoryId;

    @NotBlank(message = "Descripción es requerida")
    private String description;

    @NotNull(message = "Precio es requerido")
    @Positive(message = "Precio debe ser positivo")
    private Double price;

    @NotNull(message = "Peso es requerido")
    @Positive(message = "Peso debe ser positivo")
    private Double weight;

    @NotNull(message = "Volumen es requerido")
    @Positive(message = "Volumen debe ser positivo")
    private Double volume;

    @NotBlank(message = "Origen es requerido")
    private String origin;

    @NotBlank(message = "Destino es requerido")
    private String destination;

    @NotNull(message = "Cliente es requerido")
    @Positive(message = "clientId must be positive")
    @Setter(AccessLevel.NONE)
    private Long clientId;

    @NotBlank(message = "Transporte es requerido")
    @Setter(AccessLevel.NONE)
    private String transportId;

    @NotBlank(message = "Número de orden es requerido")
    private String orderNumber;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private LocalDateTime atDate;

    @JsonSetter("atDate")
    public void setAtDate(String atDate) {
        if (atDate == null || atDate.isBlank()) {
            this.atDate = null;
            return;
        }

        String value = atDate.trim();
        try {
            this.atDate = LocalDateTime.parse(value);
        } catch (DateTimeParseException ignored) {
            try {
                this.atDate = LocalDate.parse(value).atStartOfDay();
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("atDate must use yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss");
            }
        }
    }

    public LocalDateTime getAtDate() {
        return this.atDate;
    }

    @JsonSetter("categoryId")
    public void setCategoryId(Object value) {
        this.categoryId = parsePositiveLong(value, "categoryId", true);
    }

    @JsonSetter("clientId")
    public void setClientId(Object value) {
        this.clientId = parsePositiveLong(value, "clientId", true);
    }

    @JsonSetter("transportId")
    public void setTransportId(Object value) {
        this.transportId = parseRequiredText(value, "transportId");
    }

    private Long parsePositiveLong(Object raw, String fieldName, boolean required) {
        if (raw == null) {
            if (required) {
                throw new IllegalArgumentException(fieldName + " is required");
            }
            return null;
        }

        if (raw instanceof Number number) {
            long value = number.longValue();
            if (value <= 0L) {
                throw new IllegalArgumentException(fieldName + " must be positive");
            }
            return value;
        }

        String value = raw.toString().trim();
        if (value.isEmpty() || "null".equalsIgnoreCase(value) || "undefined".equalsIgnoreCase(value)) {
            if (required) {
                throw new IllegalArgumentException(fieldName + " is required");
            }
            return null;
        }

        try {
            long parsed = Long.parseLong(value);
            if (parsed <= 0L) {
                throw new IllegalArgumentException(fieldName + " must be positive");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a numeric id");
        }
    }

    private String parseOptionalText(Object raw) {
        if (raw == null) {
            return null;
        }

        String value = raw.toString().trim();
        if (value.isEmpty() || "null".equalsIgnoreCase(value) || "undefined".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    private String parseRequiredText(Object raw, String fieldName) {
        String parsed = parseOptionalText(raw);
        if (parsed == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return parsed;
    }

}
