package core.cibertec.ms_clientes.application.event;

public record ClientCreatedEvent(
        Long id,
        String companyCode,
        String companyName,
        String email
) {}
