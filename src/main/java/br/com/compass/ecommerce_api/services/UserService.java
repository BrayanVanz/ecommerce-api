package br.com.compass.ecommerce_api.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.User;
import br.com.compass.ecommerce_api.exceptions.EmailUniqueViolationException;
import br.com.compass.ecommerce_api.exceptions.EntityNotFoundException;
import br.com.compass.ecommerce_api.exceptions.PasswordInvalidException;
import br.com.compass.ecommerce_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailUniqueViolationException(String.format("Email {%s} is already registered", user.getEmail()));
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(String.format("User {%d} found", id))
        );
    }

    @Transactional
    public User updatePassword(Long id, String currentPassword, String newPassword, String confirmedPassword) {
        if (!newPassword.equals(confirmedPassword)) {
            throw new PasswordInvalidException("New password must be equal to the confirmed password");
        }

        User user = findById(id);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordInvalidException("Wrong password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
