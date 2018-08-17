package br.com.siecola.salesprovider.exception;

public class OrderNotFoundException extends Exception {
    private String message;

    public OrderNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}