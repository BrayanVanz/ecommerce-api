package br.com.compass.ecommerce_api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.compass.ecommerce_api.entities.Stock;
import br.com.compass.ecommerce_api.projections.StockProjection;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("SELECT s FROM Stock s")
    Page<StockProjection> findAllPageable(Pageable pageable);
}
