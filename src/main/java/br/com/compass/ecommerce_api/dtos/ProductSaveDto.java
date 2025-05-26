package br.com.compass.ecommerce_api.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
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
public class ProductSaveDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Positive(message = "Amount must be a positive number")
    private BigDecimal amount;
}
