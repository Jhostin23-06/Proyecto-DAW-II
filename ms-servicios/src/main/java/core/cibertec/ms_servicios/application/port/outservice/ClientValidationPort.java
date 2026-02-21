package core.cibertec.ms_servicios.application.port.outservice;

public interface ClientValidationPort {
    boolean existsById(Long clientId);
}
