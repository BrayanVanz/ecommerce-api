package br.com.compass.ecommerce_api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class StockSaveDto {

    @NotNull
    private Long productId;

    @NotNull
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}
