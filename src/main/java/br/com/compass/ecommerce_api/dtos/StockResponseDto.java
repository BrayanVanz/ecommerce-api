package br.com.compass.ecommerce_api.dtos;

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
public class StockResponseDto {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
}
