package br.com.compass.ecommerce_api.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.compass.ecommerce_api.entities.Stock;
import br.com.compass.ecommerce_api.projections.StockProjection;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);

    @Query("SELECT s FROM Stock s WHERE s.quantity < 10")
    Page<StockProjection> findLowStock(Pageable pageable);

    @Query("SELECT s FROM Stock s")
    Page<StockProjection> findAllPageable(Pageable pageable);
}
