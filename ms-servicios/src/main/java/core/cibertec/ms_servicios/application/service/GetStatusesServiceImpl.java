package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.StatusPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.GetStatusesPort;
import core.cibertec.ms_servicios.domain.bean.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetStatusesServiceImpl implements GetStatusesPort {

    private final StatusPersistencePort statusPersistencePort;

    @Override
    public List<Status> getAllStatuses() {
        return statusPersistencePort.findAllStatuses();
    }
}
