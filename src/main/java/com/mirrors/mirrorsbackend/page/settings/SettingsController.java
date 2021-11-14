package com.mirrors.mirrorsbackend.page.settings;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.page.settings.request.PasswordRequest;
import com.mirrors.mirrorsbackend.page.settings.request.PersonalInfoRequest;
import com.mirrors.mirrorsbackend.page.settings.request.PhoneNumberRequest;
import com.mirrors.mirrorsbackend.page.settings.request.ShippingAddressRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

    private final MarketplaceUserRepository marketplaceUserRepository;
    private final SettingsService settingsService;

    @GetMapping({"", "/personal", "/password", "/phone", "/address"})
    public ModelAndView settingsPage(@AuthenticationPrincipal MarketplaceUser user, Model model) {
        Optional<MarketplaceUser> optionalUser = marketplaceUserRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            model.addAttribute("user", user);
        } else throw new IllegalStateException("Couldn't load settings!");
        return new ModelAndView("/settings");
    }

    @PostMapping("/personal")
    public ModelAndView savePersonalInfo(PersonalInfoRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changePersonalInfo(request, user);
    }

    @PostMapping("/password")
    public ModelAndView saveNewPassword(PasswordRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changePassword(request, user);
    }

    @PostMapping("/phone")
    public ModelAndView savePhoneNumber(PhoneNumberRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changePhoneNumber(request, user);
    }

    @PostMapping("/address")
    public ModelAndView saveShippingAddress(ShippingAddressRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changeShippingAddress(request, user);
    }
}
