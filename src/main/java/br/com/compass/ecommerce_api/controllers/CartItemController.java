package br.com.compass.ecommerce_api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.dtos.CartItemSaveDto;
import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.mappers.CartItemMapper;
import br.com.compass.ecommerce_api.dtos.mappers.PageableMapper;
import br.com.compass.ecommerce_api.entities.CartItem;
import br.com.compass.ecommerce_api.projections.CartItemProjection;
import br.com.compass.ecommerce_api.services.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cart")
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> addToCart(@Valid @RequestBody CartItemSaveDto dto) {
        CartItem cartItem = CartItemMapper.toCart(dto);
        cartItemService.addToCart(cartItem);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR ( hasAuthority('CLIENT') AND #id == authentication.principal.id )")
    public ResponseEntity<PageableDto<CartItemProjection>> findAll(@PathVariable Long id, @PageableDefault(size = 3) Pageable pageable) {
        Page<CartItemProjection> cart = cartItemService.getCart(id, pageable);
        return ResponseEntity.ok(PageableMapper.toDto(cart));
    }
}
