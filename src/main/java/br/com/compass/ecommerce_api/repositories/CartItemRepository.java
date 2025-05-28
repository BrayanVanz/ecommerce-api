package br.com.compass.ecommerce_api.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.compass.ecommerce_api.entities.CartItem;
import br.com.compass.ecommerce_api.projections.CartItemProjection;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Page<CartItemProjection> findByUserId(Long userId, Pageable pageable);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
