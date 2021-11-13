package com.mirrors.mirrorsbackend.page.login.password_reset;

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
        return passwordResetTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return passwordResetTokenRepository.updateChangedAt(token, LocalDateTime.now());
    }
}
