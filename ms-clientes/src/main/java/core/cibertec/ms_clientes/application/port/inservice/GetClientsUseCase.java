package core.cibertec.ms_clientes.application.port.inservice;

import java.util.List;
import core.cibertec.ms_clientes.domain.model.Client;

public interface GetClientsUseCase {
    List<Client> getClients(Long id, String companyCode, String companyName, String address,
                            String contactName, String email, String phone);
}
