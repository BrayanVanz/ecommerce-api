package br.com.compass.ecommerce_api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.User;
import br.com.compass.ecommerce_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
