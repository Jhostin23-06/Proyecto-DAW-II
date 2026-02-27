package core.cibertec.ms_clientes.infrastructure.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClientCreateRequest(
        @NotBlank(message = "RUC es requerido")
        String companyCode,
        @NotBlank(message = "Compañía es requerida")
        String companyName,
        @NotBlank(message = "Dirección es requerido")
        String address,
        @NotBlank(message = "Nombre de contacto es requerido")
        String contactName,
        @NotBlank(message = "Correo es requerido")
        @Email(message = "Correo debe ser válido")
        String email,
        @NotBlank(message = "Teléfono es requerido")
        @Pattern(regexp = "^9\\d{8}$", message = "Teléfono debe iniciar con 9 y tener 9 dígitos")
        String phone
) {}
