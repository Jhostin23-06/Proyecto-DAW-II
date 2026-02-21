package core.cibertec.ms_clientes.infrastructure.persistence.adapter;

import core.cibertec.ms_clientes.application.port.outservice.ClientRepositoryPort;
import core.cibertec.ms_clientes.domain.model.Client;
import core.cibertec.ms_clientes.infrastructure.persistence.entity.ClientEntity;
import core.cibertec.ms_clientes.infrastructure.persistence.repository.JpaClientRepository;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public class ClientPersistenceAdapter implements ClientRepositoryPort {

    private final JpaClientRepository repo;

    public ClientPersistenceAdapter(JpaClientRepository repo) {
        this.repo = repo;
    }

    @Override
    public Client save(Client client) {
        ClientEntity entity = toEntity(client);
        ClientEntity saved = repo.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByCompanyCode(String companyCode) {
        return repo.existsByCompanyCode(companyCode);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Client> findByFilters(Long id, String companyCode, String companyName, String address,
                                     String contactName, String email, String phone) {

        // Evita Specification.where(null) (puede dar ambigüedad según versión)
        Specification<ClientEntity> spec = (root, query, cb) -> cb.conjunction();

        if (id != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("id"), id));
        if (companyCode != null) spec = spec.and(like("companyCode", companyCode));
        if (companyName != null) spec = spec.and(like("companyName", companyName));
        if (address != null) spec = spec.and(like("address", address));
        if (contactName != null) spec = spec.and(like("contactName", contactName));
        if (email != null) spec = spec.and(like("email", email));
        if (phone != null) spec = spec.and(like("phone", phone));

        return repo.findAll(spec).stream().map(this::toDomain).toList();
    }

    private Specification<ClientEntity> like(String field, String value) {
        return (r, q, cb) -> cb.like(cb.lower(r.get(field)), "%" + value.toLowerCase() + "%");
    }

    private ClientEntity toEntity(Client c) {
        ClientEntity e = new ClientEntity();
        e.setId(c.getId());
        e.setCompanyCode(c.getCompanyCode());
        e.setCompanyName(c.getCompanyName());
        e.setAddress(c.getAddress());
        e.setContactName(c.getContactName());
        e.setEmail(c.getEmail());
        e.setPhone(c.getPhone());
        return e;
    }

    private Client toDomain(ClientEntity e) {
        return new Client(
                e.getId(),
                e.getCompanyCode(),
                e.getCompanyName(),
                e.getAddress(),
                e.getContactName(),
                e.getEmail(),
                e.getPhone()
        );
    }
}
