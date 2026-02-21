package core.cibertec.ms_servicios.application.port.outservice;

import java.util.List;

public interface TransportValidationPort {
    boolean existsById(String transportId);
    List<String> findTransportIdsByUserId(String userId);
}
