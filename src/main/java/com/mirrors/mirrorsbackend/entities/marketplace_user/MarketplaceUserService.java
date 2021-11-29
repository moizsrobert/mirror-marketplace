package com.mirrors.mirrorsbackend.entities.marketplace_user;

import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostService;
import com.mirrors.mirrorsbackend.entities.password_reset_token.PasswordResetToken;
import com.mirrors.mirrorsbackend.entities.password_reset_token.PasswordResetTokenService;
import com.mirrors.mirrorsbackend.entities.email_confirmation_token.ConfirmationToken;
import com.mirrors.mirrorsbackend.entities.email_confirmation_token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public MarketplaceUser getUserById(String id) {
        return marketplaceUserRepository.getById(id);
    }

    public MarketplaceUser getUserFromContext() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (marketplaceUserRepository.findByEmail(email).isPresent())
            return marketplaceUserRepository.findByEmail(email).get();
        else
            throw  new IllegalStateException("Failed to find user!");
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

    public MarketplaceUserInformation getUserInformation(MarketplaceUser user) {
        return new MarketplaceUserInformation(user);
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

    public void enableUser(String email) {
        marketplaceUserRepository.enableMarketplaceUser(email);
    }

    public void disableUser(MarketplaceUser user) {
        marketplaceUserRepository.disableMarketplaceUser(user.getId());
    }

    public void deleteUser(MarketplaceUser user) {
        marketplaceUserRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return marketplaceUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Failed to find user!"));
    }
}
