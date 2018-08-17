package br.com.siecola.messageprovider.model;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

public class OrderInfo {
    @NotNull
    private long id;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String status;

    @NotNull
    private String reason;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
