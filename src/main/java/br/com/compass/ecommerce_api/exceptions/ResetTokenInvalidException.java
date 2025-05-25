package br.com.compass.ecommerce_api.exceptions;

public class ResetTokenInvalidException extends RuntimeException {

    public ResetTokenInvalidException(String message) {
        super(message);
    }
}
