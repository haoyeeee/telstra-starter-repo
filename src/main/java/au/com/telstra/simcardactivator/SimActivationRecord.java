package au.com.telstra.simcardactivator;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class SimActivationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String iccid;
    private String customerEmail;
    private boolean active;

    public SimActivationRecord() {
    }

    public SimActivationRecord(String iccid, String customerEmail, boolean active) {
        this.iccid = iccid;
        this.customerEmail = customerEmail;
        this.active = active;
    }

    // Getters
    public Long getId() { return id; }
    public String getIccid() { return iccid; }
    public String getCustomerEmail() { return customerEmail; }
    public boolean isActive() { return active; }

    // Setters
    public void setIccid(String iccid) { this.iccid = iccid; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setActive(boolean active) { this.active = active; }
}
