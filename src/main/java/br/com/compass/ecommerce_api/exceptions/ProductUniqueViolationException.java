package br.com.compass.ecommerce_api.exceptions;

public class ProductUniqueViolationException extends RuntimeException {

    public ProductUniqueViolationException(String message) {
        super(message);
    }
}
