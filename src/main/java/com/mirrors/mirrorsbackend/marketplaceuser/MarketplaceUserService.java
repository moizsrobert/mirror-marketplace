package com.mirrors.mirrorsbackend.marketplaceuser;

import com.mirrors.mirrorsbackend.mvc.login.password_reset.token.PasswordResetToken;
import com.mirrors.mirrorsbackend.mvc.login.password_reset.token.PasswordResetTokenService;
import com.mirrors.mirrorsbackend.mvc.registration.email_confirmation.ConfirmationToken;
import com.mirrors.mirrorsbackend.mvc.registration.email_confirmation.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MarketplaceUserService implements UserDetailsService {

    private final static Integer TOKEN_EXPIRATION_IN_MINUTES = 120;

    private final MarketplaceUserRepository marketplaceUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return marketplaceUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found!", email)));
    }

    public String signUpUser(MarketplaceUser marketplaceUser) {
        boolean userExists = marketplaceUserRepository
                .findByEmail(marketplaceUser.getEmail())
                .isPresent();

        String encodedPassword = bCryptPasswordEncoder.encode(marketplaceUser.getPassword());
        marketplaceUser.setPassword(encodedPassword);

        if (userExists) {
            MarketplaceUser userInDB = marketplaceUserRepository.findByEmail(marketplaceUser.getEmail()).get();
            if (encodedPassword.equals(userInDB.getPassword())) {
                return createConfirmationTokenForUser(marketplaceUser);
            }

            throw new IllegalStateException("Email is already taken!");
        }

        marketplaceUserRepository.save(marketplaceUser);
        return createConfirmationTokenForUser(marketplaceUser);
    }

    private String createConfirmationTokenForUser(MarketplaceUser marketplaceUser) {
        ConfirmationToken confirmationToken = new ConfirmationToken(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_IN_MINUTES),
                marketplaceUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return confirmationToken.getToken();
    }

    public String createPasswordResetToken(MarketplaceUser marketplaceUser) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60 * 24),
                marketplaceUser);
        passwordResetTokenService.savePasswordResetToken(passwordResetToken);

        return passwordResetToken.getToken();
    }

    public void enableMarketplaceUser(String email) {
        marketplaceUserRepository.enableMarketplaceUser(email);
    }
}
