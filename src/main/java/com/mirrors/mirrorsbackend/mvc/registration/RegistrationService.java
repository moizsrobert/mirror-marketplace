package com.mirrors.mirrorsbackend.mvc.registration;

import com.mirrors.mirrorsbackend.exception.TokenException;
import com.mirrors.mirrorsbackend.mvc.settings.CountryEnum;
import com.mirrors.mirrorsbackend.utils.EmailSender;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRole;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserService;
import com.mirrors.mirrorsbackend.mvc.registration.email_confirmation.ConfirmationToken;
import com.mirrors.mirrorsbackend.mvc.registration.email_confirmation.ConfirmationTokenService;
import com.mirrors.mirrorsbackend.utils.EmailValidator;
import com.mirrors.mirrorsbackend.utils.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            throw new IllegalStateException("Password must contain " +
                    "atleast 1 uppercase letter, " +
                    "1 lowercase letter, " +
                    "1 number " +
                    "and 8 characters");
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new IllegalStateException("Passwords don't match!");

        MarketplaceUser marketplaceUser = new MarketplaceUser();
        String defaultName = request.getEmail().split("@")[0];
        marketplaceUser.setEmail(request.getEmail());
        marketplaceUser.setPassword(request.getPassword());
        marketplaceUser.setMarketplaceUserRole(MarketplaceUserRole.USER);
        marketplaceUser.setDisplayName(defaultName);
        marketplaceUser.setCountry(CountryEnum.NOTHING_SELECTED);

        String token = marketplaceUserService.signUpUser(marketplaceUser);
        String link = "http://www.localhost:8080/api/registration/confirm?token=" + token;
        emailSender.send(request.getEmail(), "Confirm your marketplace email", buildEmail(link));

        return new ModelAndView("redirect:/api/login?registration_successful");
    }

    @Transactional
    public ModelAndView confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new TokenException("Token not found!", token));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new TokenException("Token already confirmed!", token);
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new TokenException("Token is expired!", token);
        }

        confirmationTokenService.setConfirmedAt(token);
        marketplaceUserService.enableMarketplaceUser(confirmationToken.getMarketplaceUser().getEmail());
        return new ModelAndView("redirect:/api/login?email_confirmed");
    }

    private String buildEmail(String link) {
        return "Your confirmation link (expires in 60 minutes):<br>" + link;
    }
}
