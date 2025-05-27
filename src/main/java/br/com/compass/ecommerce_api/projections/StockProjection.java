package br.com.compass.ecommerce_api.projections;

public interface StockProjection {

    Long getId();
    Integer getQuantity();
    ProductProjection getProduct();
}
