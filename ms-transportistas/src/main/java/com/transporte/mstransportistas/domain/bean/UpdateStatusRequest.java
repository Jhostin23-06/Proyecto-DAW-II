package com.transporte.mstransportistas.domain.bean;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El estado del transporte es requerido")
    private String transportStatus; // String para recibir del request
    private String location;
    private String reason;

}
