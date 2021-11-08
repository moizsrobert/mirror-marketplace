package com.mirrors.mirrorsbackend.marketplaceuser.login;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserService;
import com.mirrors.mirrorsbackend.marketplaceuser.login.passwordreset.PasswordResetRequest;
import com.mirrors.mirrorsbackend.marketplaceuser.login.passwordreset.PasswordResetToken;
import com.mirrors.mirrorsbackend.marketplaceuser.login.passwordreset.PasswordResetTokenService;
import com.mirrors.mirrorsbackend.marketplaceuser.utils.EmailSender;
import com.mirrors.mirrorsbackend.marketplaceuser.utils.PasswordValidator;
import com.mirrors.mirrorsbackend.security.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class LoginService {

    private final MarketplaceUserService marketplaceUserService;
    private final MarketplaceUserRepository marketplaceUserRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailSender emailSender;

    public ModelAndView sendPasswordResetEmail(String email) {
        MarketplaceUser marketplaceUser;

        if (marketplaceUserRepository.findByEmail(email).isPresent())
            marketplaceUser = marketplaceUserRepository.findByEmail(email).get();
        else
            throw new IllegalStateException("Email not found!");

        String token = marketplaceUserService.createPasswordResetToken(marketplaceUser);
        String link = "localhost:8080/api/password-reset?token=" + token;
        emailSender.send(email, buildEmail(link));

        return new ModelAndView("redirect:/api/login?reset_email_sent");
    }

    @Transactional
    public ModelAndView confirmToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found!"));

        if (passwordResetToken.getChangedAt() != null)
            throw new IllegalStateException("Link has been used already!");

        LocalDateTime expiredAt = passwordResetToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Link has expired!");

        return new ModelAndView("/api/password-reset");
    }

    public ModelAndView changePassword(PasswordResetRequest passwordResetRequest) {
        PasswordResetToken passwordResetToken = passwordResetTokenService
                .getToken(passwordResetRequest.getToken())
                .orElseThrow(() -> new IllegalStateException("Token Error: Token not found!"));

        if (passwordResetToken.getChangedAt() != null) {
            throw new IllegalStateException("Token Error: Token already used!");
        }

        LocalDateTime expiredAt = passwordResetToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token Error: Token is expired!");
        }

        if (!passwordResetRequest.getPassword().equals(passwordResetRequest.getConfirmPassword()))
            throw new IllegalStateException("Passwords don't match!");

        PasswordValidator validator = new PasswordValidator();
        if (!validator.test(passwordResetRequest.getPassword()))
            throw new IllegalStateException("""
                            Password must contain:
                            - Atleast 1 uppercase letter
                            - Atleast 1 lowercase letter
                            - Atleast 1 numbers
                            - Atleast 8 characters
                            """
            );

        MarketplaceUser marketplaceUser = passwordResetToken.getMarketplaceUser();
        String encryptedPassword = new PasswordEncoder()
                .bCryptPasswordEncoder()
                .encode(passwordResetRequest.getPassword());
        marketplaceUserRepository.changePassword(marketplaceUser.getEmail(), encryptedPassword);
        passwordResetTokenService.setConfirmedAt(passwordResetRequest.getToken());

        return new ModelAndView("redirect:/api/login?password_changed");
    }

    private String buildEmail(String link) {
        return "You can change your password here (expires in 24 hours):<br>" + link;
    }
}
