package core.cibertec.ms_clientes.infrastructure.controller.dto;

public record ClientPatchRequest(
        String companyCode,
        String companyName,
        String address,
        String contactName,
        String email,
        String phone
) {}