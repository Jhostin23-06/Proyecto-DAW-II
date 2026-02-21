package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.StatusPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.DeleteStatusPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStatusServiceImpl implements DeleteStatusPort {

    private final StatusPersistencePort statusPersistencePort;

    @Override
    public boolean deleteStatus(Long statusId) {
        return statusPersistencePort.deleteById(statusId);
    }
}
