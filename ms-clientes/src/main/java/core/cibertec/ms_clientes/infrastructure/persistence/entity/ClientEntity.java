package core.cibertec.ms_clientes.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clients", uniqueConstraints = {
        @UniqueConstraint(name="uk_clients_company_code", columnNames = "company_code")
})
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="company_code", nullable = false, length = 11)
    private String companyCode;

    @Column(name="company_name", nullable = false)
    private String companyName;

    private String address;

    @Column(name="contact_name")
    private String contactName;

    private String email;
    private String phone;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
