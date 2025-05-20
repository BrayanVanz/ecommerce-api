package br.com.compass.ecommerce_api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.entities.User;
import br.com.compass.ecommerce_api.services.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> save(User user) {
        User newUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
