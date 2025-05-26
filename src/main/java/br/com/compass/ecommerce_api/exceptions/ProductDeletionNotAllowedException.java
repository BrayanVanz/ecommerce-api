package br.com.compass.ecommerce_api.exceptions;

public class ProductDeletionNotAllowedException extends RuntimeException {

    public ProductDeletionNotAllowedException(String message) {
        super(message);
    }
}
