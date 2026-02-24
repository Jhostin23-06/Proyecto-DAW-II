package core.cibertec.ms_servicios.application.port.outservice;

import java.util.List;
import java.util.Optional;

public interface TransportValidationPort {
    boolean existsById(String transportId);
    boolean isAvailableById(String transportId);
    Optional<String> getStatusById(String transportId);
    List<String> findTransportIdsByUserId(String userId);
}
