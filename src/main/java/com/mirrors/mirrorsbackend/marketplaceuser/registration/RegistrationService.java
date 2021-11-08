package com.mirrors.mirrorsbackend.marketplaceuser.registration;

import com.mirrors.mirrorsbackend.marketplaceuser.utils.EmailSender;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRole;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserService;
import com.mirrors.mirrorsbackend.marketplaceuser.registration.token.ConfirmationToken;
import com.mirrors.mirrorsbackend.marketplaceuser.registration.token.ConfirmationTokenService;
import com.mirrors.mirrorsbackend.marketplaceuser.utils.EmailValidator;
import com.mirrors.mirrorsbackend.marketplaceuser.utils.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final MarketplaceUserService marketplaceUserService;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    public ModelAndView register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail)
            throw new IllegalStateException("Email is invalid!");

        boolean isValidPassword = passwordValidator.test(request.getPassword());
        if (!isValidPassword)
            throw new IllegalStateException("""
                            Password must contain:
                            - Atleast 1 uppercase letter
                            - Atleast 1 lowercase letter
                            - Atleast 1 numbers
                            - Atleast 8 characters
                            """
            );

        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new IllegalStateException("Passwords don't match!");

        MarketplaceUser marketplaceUser = new MarketplaceUser(
                request.getEmail(),
                request.getPassword(),
                MarketplaceUserRole.USER
        );
        String token = marketplaceUserService.signUpUser(marketplaceUser);

        String link = "http://localhost:8080/api/registration/confirm?token=" + token;
        emailSender.send(request.getEmail(), buildEmail(link));

        return new ModelAndView("redirect:/api/login?registration_successful");
    }

    @Transactional
    public ModelAndView confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token Error: Token not found!"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Token Error: Token already confirmed!");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token Error: Token is expired!");
        }

        confirmationTokenService.setConfirmedAt(token);
        marketplaceUserService.enableMarketplaceUser(confirmationToken.getMarketplaceUser().getEmail());
        return new ModelAndView("redirect:/api/login?email_confirmed");
    }

    private String buildEmail(String link) {
        return "Your confirmation link (expires in 60 minutes):<br>" + link;
    }
}
