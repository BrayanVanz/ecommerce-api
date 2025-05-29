package br.com.compass.ecommerce_api.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.compass.ecommerce_api.entities.Purchase;
import br.com.compass.ecommerce_api.projections.TopBuyerProjection;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.purchaseDate >= :start AND p.purchaseDate <= :end")
    BigDecimal getTotalAmount(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT u.id AS userId, u.name AS userName, COUNT(p.id) AS purchaseCount
    FROM Purchase p
    JOIN p.user u
    GROUP BY u.id, u.name
    ORDER BY COUNT(p.id) DESC
    """)
    Page<TopBuyerProjection> findTopBuyers(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.purchaseDate >= :start AND p.purchaseDate <= :end")
    Integer getTotalPurchases(LocalDateTime start, LocalDateTime end);
}
