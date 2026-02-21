package core.cibertec.ms_clientes.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import core.cibertec.ms_clientes.infrastructure.persistence.entity.ClientEntity;

public interface JpaClientRepository
        extends JpaRepository<ClientEntity, Long>,
                JpaSpecificationExecutor<ClientEntity> {

    boolean existsByCompanyCode(String companyCode);
}
