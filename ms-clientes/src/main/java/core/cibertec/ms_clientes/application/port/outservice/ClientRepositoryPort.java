package core.cibertec.ms_clientes.application.port.outservice;

import core.cibertec.ms_clientes.domain.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientRepositoryPort {
    Client save(Client client);
    Optional<Client> findById(Long id);
    boolean existsByCompanyCode(String companyCode);
    void deleteById(Long id);

    List<Client> findByFilters(
            Long id,
            String companyCode,
            String companyName,
            String address,
            String contactName,
            String email,
            String phone
    );
}
