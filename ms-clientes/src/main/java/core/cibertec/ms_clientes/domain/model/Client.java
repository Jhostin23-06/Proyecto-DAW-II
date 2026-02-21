package core.cibertec.ms_clientes.domain.model;

public class Client {
    private Long id;
    private String companyCode;   // Lo estoy poniendo como RUC
    private String companyName;
    private String address;
    private String contactName;
    private String email;
    private String phone;

    public Client(Long id, String companyCode, String companyName, String address,
                  String contactName, String email, String phone) {
        this.id = id;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.address = address;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
    }

    public void validate() {
        if (companyCode == null || !companyCode.matches("\\d{11}")) {
            throw new IllegalArgumentException("RUC debe tener 11 dígitos.");
        }
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Compañía es obligatorio.");
        }
        // Validar que el telefono empiece con 9 y tenga 9 dígitos
        if (phone != null && !phone.isBlank() && !phone.matches("9\\d{8}")) {
            throw new IllegalArgumentException("Teléfono no es válido. Debe empezar con 9 y tener 9 dígitos.");
        }
        // Validar que la direccion sea obligatoria
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Dirección es obligatoria.");
        }
        if (email != null && !email.isBlank() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Email no es válido.");
        }
    }

    public void patch(String companyCode, String companyName, String address,
                      String contactName, String email, String phone) {
        if (companyCode != null) this.companyCode = companyCode;
        if (companyName != null) this.companyName = companyName;
        if (address != null) this.address = address;
        if (contactName != null) this.contactName = contactName;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyCode() { return companyCode; }
    public String getCompanyName() { return companyName; }
    public String getAddress() { return address; }
    public String getContactName() { return contactName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
