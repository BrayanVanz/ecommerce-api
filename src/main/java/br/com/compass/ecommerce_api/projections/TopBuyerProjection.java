package br.com.compass.ecommerce_api.projections;

public interface TopBuyerProjection {

    Long getUserId();
    String getUserName();
    Long getPurchaseCount();
}
