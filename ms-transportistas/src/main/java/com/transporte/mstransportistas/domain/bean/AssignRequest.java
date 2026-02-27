package com.transporte.mstransportistas.domain.bean;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRequest {
    @NotBlank(message = "Usuario es requerido")
    private String userId;
}
