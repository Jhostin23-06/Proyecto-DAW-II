package core.cibertec.ms_servicios.application.port.usecase;

import core.cibertec.ms_servicios.domain.bean.Status;

import java.util.List;

public interface GetStatusesPort {
    List<Status> getAllStatuses();
}
