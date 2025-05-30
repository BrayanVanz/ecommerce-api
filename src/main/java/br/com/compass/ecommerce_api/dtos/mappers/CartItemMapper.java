package br.com.compass.ecommerce_api.dtos.mappers;

import br.com.compass.ecommerce_api.dtos.CartItemSaveDto;
import br.com.compass.ecommerce_api.entities.CartItem;

public class CartItemMapper {

    public static CartItem toCart(CartItemSaveDto dto) {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(dto.getQuantity());
        return cartItem;
    }
}
