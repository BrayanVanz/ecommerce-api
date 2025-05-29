package br.com.compass.ecommerce_api.exceptions;

public class PurchasePeriodInvalidException extends RuntimeException {

    public PurchasePeriodInvalidException(String message) {
        super(message);
    }
}
