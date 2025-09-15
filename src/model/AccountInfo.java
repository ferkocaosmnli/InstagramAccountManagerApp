package model;

import java.sql.Date;

public class AccountInfo {
    private int id;
    private String username;
    private String password;
    private String email;
    private String emailPassword;
    private String recoveryEmail;
    private String emailOnayli;   // yes / no
    private String phone;
    private String phoneKayitli;  // yes / no
    private Date accountCreated;
    private String notes;

    // ---- GETTER & SETTER ----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmailPassword() { return emailPassword; }
    public void setEmailPassword(String emailPassword) { this.emailPassword = emailPassword; }

    public String getRecoveryEmail() { return recoveryEmail; }
    public void setRecoveryEmail(String recoveryEmail) { this.recoveryEmail = recoveryEmail; }

    public String getEmailOnayli() { return emailOnayli; }
    public void setEmailOnayli(String emailOnayli) { this.emailOnayli = emailOnayli; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPhoneKayitli() { return phoneKayitli; }
    public void setPhoneKayitli(String phoneKayitli) { this.phoneKayitli = phoneKayitli; }

    public Date getAccountCreated() { return accountCreated; }
    public void setAccountCreated(Date accountCreated) { this.accountCreated = accountCreated; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
