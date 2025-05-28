package br.com.compass.ecommerce_api.projections;

import java.math.BigDecimal;

public interface CartItemProjection {

    Long getId();
    Integer getQuantity();
    ProductInfo getProduct();

    interface ProductInfo {
    
        Long getId();
        String getName();
        BigDecimal getAmount();
    }
}
