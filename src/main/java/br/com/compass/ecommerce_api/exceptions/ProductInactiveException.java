package br.com.compass.ecommerce_api.exceptions;

public class ProductInactiveException extends RuntimeException {

    public ProductInactiveException(String message) {
        super(message);
    }
}
