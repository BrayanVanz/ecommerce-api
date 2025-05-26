package br.com.compass.ecommerce_api.projections;

import java.math.BigDecimal;

public interface ProductProjection {

    Long getId();
    String getName();
    String getDescription();
    BigDecimal getAmount();
    Integer getTimesPurchased();
}
