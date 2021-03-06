package com.mirrors.mirrorsbackend.controller.registration;

import com.mirrors.mirrorsbackend.exception.MarketplaceException;
import com.mirrors.mirrorsbackend.exception.TokenException;
import com.mirrors.mirrorsbackend.controller.profile.CountryEnum;
import com.mirrors.mirrorsbackend.util.EmailSender;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserRole;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.model.email_confirmation_token.ConfirmationToken;
import com.mirrors.mirrorsbackend.model.email_confirmation_token.ConfirmationTokenService;
import com.mirrors.mirrorsbackend.util.EmailValidator;
import com.mirrors.mirrorsbackend.util.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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

    @SneakyThrows
    public void register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail)
            throw new MarketplaceException("Email is invalid!");

        boolean isValidPassword = passwordValidator.test(request.getPassword());
        if (!isValidPassword)
            throw new MarketplaceException("Password must contain " +
                    "atleast 1 uppercase letter, " +
                    "1 lowercase letter, " +
                    "1 number " +
                    "and 8 characters");
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new MarketplaceException("Passwords don't match!");

        MarketplaceUser marketplaceUser = new MarketplaceUser();
        String defaultName = request.getEmail().split("@")[0];
        marketplaceUser.setEmail(request.getEmail());
        marketplaceUser.setPassword(request.getPassword());
        marketplaceUser.setMarketplaceUserRole(MarketplaceUserRole.USER);
        marketplaceUser.setDisplayName(defaultName);
        marketplaceUser.setCountry(CountryEnum.NOTHING_SELECTED);

        String token = marketplaceUserService.signUpUser(marketplaceUser);
        String link = "http://46.107.88.215:8080/api/registration/confirm?token=" + token;
        emailSender.send(request.getEmail(), "Confirm your marketplace email", buildEmail(link));
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
        marketplaceUserService.enableUser(confirmationToken.getMarketplaceUser().getEmail());
        return new ModelAndView("redirect:/api/login?email_confirmed");
    }

    private String buildEmail(String link) {
        return "Your confirmation link (expires in 60 minutes):<br>" + link;
    }
}
