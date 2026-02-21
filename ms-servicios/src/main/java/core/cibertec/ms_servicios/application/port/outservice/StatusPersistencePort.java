package core.cibertec.ms_servicios.application.port.outservice;

import core.cibertec.ms_servicios.domain.bean.Status;

import java.util.List;

public interface StatusPersistencePort {
    List<Status> findAllStatuses();
    Status createStatus(String statusName);
}
