package br.com.compass.ecommerce_api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.CartItem;
import br.com.compass.ecommerce_api.exceptions.EntityNotFoundException;
import br.com.compass.ecommerce_api.projections.CartItemProjection;
import br.com.compass.ecommerce_api.repositories.CartItemRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    @Transactional
    public void addToCart(CartItem newCartItem) {
        if (cartItemRepository.existsByUserIdAndProductId(newCartItem.getUser().getId(), newCartItem.getProduct().getId())) {
            CartItem cartItem = findById(newCartItem.getId());
            cartItem.setQuantity(cartItem.getQuantity() + newCartItem.getQuantity());
        } else {
            CartItem cartItem = findByUserIdAndProductId(newCartItem.getUser().getId(), newCartItem.getProduct().getId());
            cartItem.setQuantity(cartItem.getQuantity() + newCartItem.getQuantity());
        }
    }

    @Transactional(readOnly = true)
    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Cart item {%d} not found", id)
            )
        );
    }

    @Transactional(readOnly = true)
    public CartItem findByUserIdAndProductId(Long userId, Long productId) {
        return cartItemRepository.findByUserIdAndProductId(userId, productId).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Cart item not found for user {%d}", userId)
            )
        );
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Transactional
    public Page<CartItemProjection> getCart(Long id, Pageable pageable) {
        return cartItemRepository.findByUserId(id, pageable);
    }
}
