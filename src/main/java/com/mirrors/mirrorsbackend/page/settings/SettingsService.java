package com.mirrors.mirrorsbackend.page.settings;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserService;
import com.mirrors.mirrorsbackend.utils.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
@AllArgsConstructor
public class SettingsService {

    private MarketplaceUserRepository marketplaceUserRepository;
    private MarketplaceUserService marketplaceUserService;
    private EmailSender emailSender;

    public ModelAndView changeSettings(SettingsRequest request, MarketplaceUser userFromSession) {
        request.validate();
        marketplaceUserRepository.changeSettings(
                request.getDisplayName(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getCountry(),
                request.getCity(),
                request.getStreetAddress(),
                request.getZipCode(),
                userFromSession.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }

    public ModelAndView sendPasswordResetEmail(MarketplaceUser marketplaceUser) {
        String token = marketplaceUserService.createPasswordResetToken(marketplaceUser);
        String link = "http://www.localhost:8080/api/password-reset?token=" + token;
        emailSender.send(marketplaceUser.getEmail(), "Reset your marketplace password", buildEmail(link));
        return new ModelAndView("redirect:/settings?email_sent");
    }

    private String buildEmail(String link) {
        return "You can change your password here (expires in 24 hours):<br>" + link;
    }
}
