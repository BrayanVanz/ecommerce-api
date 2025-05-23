package br.com.compass.ecommerce_api.dtos;

import br.com.compass.ecommerce_api.enums.UserRole;
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
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
