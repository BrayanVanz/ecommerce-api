package br.com.compass.ecommerce_api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.compass.ecommerce_api.entities.Product;
import br.com.compass.ecommerce_api.projections.ProductProjection;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p ORDER BY p.timesPurchased DESC")
    Page<ProductProjection> findBestSelling(Pageable pageable);

    @Query("SELECT p FROM Product p")
    Page<ProductProjection> findAllPageable(Pageable pageable);
}
