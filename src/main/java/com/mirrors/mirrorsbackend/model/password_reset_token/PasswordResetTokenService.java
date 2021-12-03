package com.mirrors.mirrorsbackend.model.password_reset_token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public void savePasswordResetToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public Optional<PasswordResetToken> getToken(String token) {
        return passwordResetTokenRepository.findById(token);
    }

    public void setConfirmedAt(String token) {
        passwordResetTokenRepository.updateChangedAt(token, LocalDateTime.now());
    }
}
