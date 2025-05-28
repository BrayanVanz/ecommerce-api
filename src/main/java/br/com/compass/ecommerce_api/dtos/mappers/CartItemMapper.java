package br.com.compass.ecommerce_api.dtos.mappers;

import org.modelmapper.ModelMapper;

import br.com.compass.ecommerce_api.dtos.CartItemSaveDto;
import br.com.compass.ecommerce_api.entities.CartItem;

public class CartItemMapper {

    public static CartItem toCart(CartItemSaveDto dto) {
        return new ModelMapper().map(dto, CartItem.class);
    }
}
