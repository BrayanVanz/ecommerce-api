package br.com.compass.ecommerce_api.dtos.mappers;

import org.modelmapper.ModelMapper;

import br.com.compass.ecommerce_api.dtos.ProductResponseDto;
import br.com.compass.ecommerce_api.dtos.ProductSaveDto;
import br.com.compass.ecommerce_api.entities.Product;

public class ProductMapper {

    public static Product toProduct(ProductSaveDto dto) {
        return new ModelMapper().map(dto, Product.class);
    }

    public static ProductResponseDto toDto(Product product) {
        return new ModelMapper().map(product, ProductResponseDto.class);
    }
}
