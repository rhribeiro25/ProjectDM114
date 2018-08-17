package br.com.siecola.salesprovider.exception;

public class ProductNotFoundException extends Exception {
    private String message;

    public ProductNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}