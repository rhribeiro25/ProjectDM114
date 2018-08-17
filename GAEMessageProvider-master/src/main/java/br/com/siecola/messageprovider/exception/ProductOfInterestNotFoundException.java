package br.com.siecola.messageprovider.exception;

public class ProductOfInterestNotFoundException extends Exception {
    private String message;

    public ProductOfInterestNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}