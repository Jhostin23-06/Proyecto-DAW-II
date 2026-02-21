package core.cibertec.ms_servicios.application.port.usecase;

import core.cibertec.ms_servicios.domain.bean.Status;

public interface CreateStatusPort {
    Status createStatus(String statusName);
}
