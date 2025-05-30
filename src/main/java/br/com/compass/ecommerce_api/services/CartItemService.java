package br.com.compass.ecommerce_api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.CartItem;
import br.com.compass.ecommerce_api.entities.Product;
import br.com.compass.ecommerce_api.entities.User;
import br.com.compass.ecommerce_api.enums.ProductStatus;
import br.com.compass.ecommerce_api.exceptions.EntityNotFoundException;
import br.com.compass.ecommerce_api.exceptions.ProductInactiveException;
import br.com.compass.ecommerce_api.projections.CartItemProjection;
import br.com.compass.ecommerce_api.repositories.CartItemRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional
    public void addToCart(CartItem newCartItem, Long userId, Long productId) {
        Product product = productService.findById(productId);
        User user = userService.findById(userId);

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ProductInactiveException(
                String.format("Product {%s} is inactive", product.getName())
            );
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + newCartItem.getQuantity());
        } else {
            newCartItem.setProduct(product);
            newCartItem.setUser(user);
            cartItemRepository.save(newCartItem);
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

    @Transactional(readOnly = true)
    public List<CartItem> findByUserId(Long id) {
        return cartItemRepository.findByUserId(id);
    }

    @Transactional(readOnly = true)
    public Page<CartItemProjection> getCart(Long id, Pageable pageable) {
        return cartItemRepository.findByUserId(id, pageable);
    }
}
