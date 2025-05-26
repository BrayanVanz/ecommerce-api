package br.com.compass.ecommerce_api.dtos;

import java.math.BigDecimal;

import br.com.compass.ecommerce_api.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductResponseDto {

    private Long id;
    private String name;
    private String description;
    private ProductStatus status;
    private BigDecimal amount;
    private Integer timesPurchased;
}
