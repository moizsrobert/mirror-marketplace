package com.mirrors.mirrorsbackend.mvc.login.password_reset;

import com.mirrors.mirrorsbackend.exception.TokenException;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserService;
import com.mirrors.mirrorsbackend.mvc.login.password_reset.token.PasswordResetToken;
import com.mirrors.mirrorsbackend.mvc.login.password_reset.token.PasswordResetTokenService;
import com.mirrors.mirrorsbackend.utils.EmailSender;
import com.mirrors.mirrorsbackend.utils.PasswordValidator;
import com.mirrors.mirrorsbackend.utils.security.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PasswordResetService {

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
        String link = "http://www.localhost:8080/api/reset-password?token=" + token;
        emailSender.send(email, "Reset your marketplace password", buildEmail(link));

        return new ModelAndView("redirect:/api/login?reset_email_sent");
    }

    @Transactional
    public ModelAndView confirmToken(String token) {
        if (token == null)
            throw new TokenException("Token not found!", token);

        PasswordResetToken passwordResetToken = passwordResetTokenService
                .getToken(token)
                .orElseThrow(() -> new TokenException("Token not found!", token));

        if (passwordResetToken.getChangedAt() != null)
            throw new TokenException("Link has been used already!", token);

        LocalDateTime expiredAt = passwordResetToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now()))
            throw new TokenException("Link has expired!", token);

        return new ModelAndView("/api/reset-password");
    }

    public ModelAndView changePassword(PasswordResetRequest passwordResetRequest) {
        PasswordResetToken passwordResetToken = passwordResetTokenService
                .getToken(passwordResetRequest.getToken())
                .orElseThrow(() -> new TokenException("Token not found!", passwordResetRequest.getToken()));

        if (passwordResetToken.getChangedAt() != null) {
            throw new TokenException("Token already used!", passwordResetRequest.getToken());
        }

        LocalDateTime expiredAt = passwordResetToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new TokenException("Token is expired!", passwordResetRequest.getToken());
        }

        if (!passwordResetRequest.getPassword().equals(passwordResetRequest.getConfirmPassword()))
            throw new IllegalStateException("Passwords don't match!");

        PasswordValidator validator = new PasswordValidator();
        if (!validator.test(passwordResetRequest.getPassword()))
            throw new IllegalStateException("Password must contain " +
                    "atleast 1 uppercase letter, " +
                    "1 lowercase letter, " +
                    "1 number " +
                    "and 8 characters");

        MarketplaceUser user = passwordResetToken.getMarketplaceUser();
        String encryptedPassword = new PasswordEncoder()
                .bCryptPasswordEncoder()
                .encode(passwordResetRequest.getPassword());
        marketplaceUserRepository.changePassword(encryptedPassword, user.getId());
        passwordResetTokenService.setConfirmedAt(passwordResetRequest.getToken());

        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
        return new ModelAndView("redirect:/api/login?password_changed");
    }

    private String buildEmail(String link) {
        return "You can change your password here (expires in 24 hours):<br>" + link;
    }
}
