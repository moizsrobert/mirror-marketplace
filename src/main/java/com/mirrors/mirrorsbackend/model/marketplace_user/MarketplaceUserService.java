package com.mirrors.mirrorsbackend.model.marketplace_user;

import com.mirrors.mirrorsbackend.controller.profile.CountryEnum;
import com.mirrors.mirrorsbackend.model.password_reset_token.PasswordResetToken;
import com.mirrors.mirrorsbackend.model.password_reset_token.PasswordResetTokenService;
import com.mirrors.mirrorsbackend.model.email_confirmation_token.ConfirmationToken;
import com.mirrors.mirrorsbackend.model.email_confirmation_token.ConfirmationTokenService;
import com.mirrors.mirrorsbackend.exception.MarketplaceException;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

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
            throw  new MarketplaceException("Failed to find user!");
    }

    public List<MarketplaceUser> getUsersFromCountry(CountryEnum country) {
        return marketplaceUserRepository.findUsersFromSameCountry(country);
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

            throw new MarketplaceException("Email is already taken!");
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

    public void lockUser(MarketplaceUser user) {
        marketplaceUserRepository.lockMarketplaceUser(user.getId());
    }

    public void deleteUser(MarketplaceUser user) {
        marketplaceUserRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return marketplaceUserRepository.findByEmail(email)
                .orElseThrow(() -> new MarketplaceException("Failed to find user!"));
    }
}
