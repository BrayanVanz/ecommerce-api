package br.com.compass.ecommerce_api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.PasswordResetToken;
import br.com.compass.ecommerce_api.repositories.PasswordTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PasswordTokenService {

    private final PasswordTokenRepository passwordTokenRepository;

    @Transactional
    public void save(PasswordResetToken resetToken) {
        passwordTokenRepository.save(resetToken);
    }

    @Transactional(readOnly = true)
    public PasswordResetToken findByToken(String token) {
        return passwordTokenRepository.findByToken(token).orElseThrow(
            () -> new EntityNotFoundException(String.format("Token {%s} not found", token))
        );
    }
}
