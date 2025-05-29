package br.com.compass.ecommerce_api.exceptions;

public class CartEmptyException extends RuntimeException {

    public CartEmptyException(String message) {
        super(message);
    }
}
