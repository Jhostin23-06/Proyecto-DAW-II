package core.cibertec.ms_clientes.application.port.inservice;

import core.cibertec.ms_clientes.domain.model.Client;

public interface PatchClientUseCase {
    Client patch(Long id, String companyCode, String companyName, String address,
                 String contactName, String email, String phone);
}
