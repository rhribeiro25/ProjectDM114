package br.com.siecola.salesprovider.exception;

public class OrderItemNotFoundException extends Exception {
    private String message;

    public OrderItemNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}