package com.mirrors.mirrorsbackend.marketplaceuser;

import com.mirrors.mirrorsbackend.marketplaceuser.login.passwordreset.PasswordResetToken;
import com.mirrors.mirrorsbackend.marketplaceuser.login.passwordreset.PasswordResetTokenService;
import com.mirrors.mirrorsbackend.marketplaceuser.registration.token.ConfirmationToken;
import com.mirrors.mirrorsbackend.marketplaceuser.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MarketplaceUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MESSAGE = "User with email %s not found!";
    private final static Integer TOKEN_EXPIRATION_IN_MINUTES = 60;

    private final MarketplaceUserRepository marketplaceUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return marketplaceUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, email)));
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

    public int enableMarketplaceUser(String email) {
        return marketplaceUserRepository.enableMarketplaceUser(email);
    }
}
