package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.StatusPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.CreateStatusPort;
import core.cibertec.ms_servicios.domain.bean.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateStatusServiceImpl implements CreateStatusPort {

    private final StatusPersistencePort statusPersistencePort;

    @Override
    public Status createStatus(String statusName) {
        return statusPersistencePort.createStatus(statusName);
    }
}
