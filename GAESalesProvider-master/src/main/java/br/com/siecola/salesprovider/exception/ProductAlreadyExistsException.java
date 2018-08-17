package br.com.siecola.salesprovider.exception;

public class ProductAlreadyExistsException extends Exception {
    private String message;

    public ProductAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}