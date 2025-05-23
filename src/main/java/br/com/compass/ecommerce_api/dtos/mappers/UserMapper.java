package br.com.compass.ecommerce_api.dtos.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import br.com.compass.ecommerce_api.dtos.UserResponseDto;
import br.com.compass.ecommerce_api.dtos.UserSaveDto;
import br.com.compass.ecommerce_api.entities.User;

public class UserMapper {

    public static User toUser(UserSaveDto dto) {
        return new ModelMapper().map(dto, User.class);
    }

    public static UserResponseDto toDto(User user) {
        return new ModelMapper().map(user, UserResponseDto.class);
    }
    
    public static List<UserResponseDto> toListDto(List<User> users) {
        return users.stream().map(user -> toDto(user)).collect(Collectors.toList());
    }
}
