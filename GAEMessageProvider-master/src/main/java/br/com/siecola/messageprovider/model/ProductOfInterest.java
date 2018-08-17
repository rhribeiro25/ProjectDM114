package br.com.siecola.messageprovider.model;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

public class ProductOfInterest {

    private long id;
    private String code;
    private double price;

    @NotNull
    @Email
    private String email;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
