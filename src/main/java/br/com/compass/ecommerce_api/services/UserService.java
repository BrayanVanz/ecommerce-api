package br.com.compass.ecommerce_api.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.PasswordResetToken;
import br.com.compass.ecommerce_api.entities.User;
import br.com.compass.ecommerce_api.enums.UserRole;
import br.com.compass.ecommerce_api.exceptions.EmailUniqueViolationException;
import br.com.compass.ecommerce_api.exceptions.EntityNotFoundException;
import br.com.compass.ecommerce_api.exceptions.PasswordInvalidException;
import br.com.compass.ecommerce_api.exceptions.ResetTokenInvalidException;
import br.com.compass.ecommerce_api.projections.UserProjection;
import br.com.compass.ecommerce_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordTokenService tokenService;

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
            () -> new EntityNotFoundException(String.format("User {%d} not found", id))
        );
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException(String.format("User {%s} not found", email))
        );

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
            .token(token)
            .user(user)
            .expiryDate(LocalDateTime.now().plusMinutes(5))
            .build();
        tokenService.save(resetToken);
        
        String resetLink = String.format("localhost:8080/password-reset/confirm?token=%s", token);
        emailService.sendEmail(email, resetLink);
    }

    @Transactional
    public void resetPassword(String token, String newPassword, String confirmedPassword) {
        if (!newPassword.equals(confirmedPassword)) {
            throw new PasswordInvalidException("New password must be equal to the confirmed password");
        }

        PasswordResetToken resetToken = tokenService.findByToken(token);
        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResetTokenInvalidException("Token expired or already used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        resetToken.setUsed(true);
    }

    @Transactional(readOnly = true)
    public Page<UserProjection> findAll(Pageable pageable) {
        return userRepository.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException(String.format("User {%s} not found", email))
        );
    }

    @Transactional(readOnly = true)
    public UserRole findRoleByEmail(String email) {
        return userRepository.findRoleByEmail(email);
    }
}
