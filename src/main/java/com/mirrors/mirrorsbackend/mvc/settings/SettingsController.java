package com.mirrors.mirrorsbackend.mvc.settings;

import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostRepository;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.mvc.settings.request.*;
import lombok.AllArgsConstructor;
import org.dom4j.rule.Mode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@RestController
@AllArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ModelAndView settingsPage(@AuthenticationPrincipal MarketplaceUser user, Model model) {
        settingsService.loadSettingsPage(user, model);
        return new ModelAndView("/settings");
    }

    @GetMapping({"/personal", "/password", "/phone", "/address", "/clear"})
    public ModelAndView redirectToSettingsPage(@AuthenticationPrincipal MarketplaceUser user, Model model) {
        settingsService.loadSettingsPage(user, model);
        return new ModelAndView("redirect:/settings");
    }

    @PostMapping("/personal")
    public ModelAndView savePersonalInfo(PersonalInfoRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changePersonalInfo(request, user, false);
    }

    @PostMapping("/password")
    public ModelAndView saveNewPassword(PasswordRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changePassword(request, user);
    }

    @PostMapping("/phone")
    public ModelAndView savePhoneNumber(PhoneNumberRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changePhoneNumber(request, user, false);
    }

    @PostMapping("/address")
    public ModelAndView saveShippingAddress(ShippingAddressRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changeShippingAddress(request, user, false);
    }

    @PostMapping("/clear")
    public ModelAndView clearSettings(SettingValueEnum value, @AuthenticationPrincipal MarketplaceUser user) {
        if (Arrays.stream(SettingValueEnum.values()).noneMatch((v) -> v.equals(value)))
            throw new IllegalStateException("Couldn't clear settings!");
        return switch (value) {
            case PERSONAL_INFO -> settingsService.changePersonalInfo(null, user, true);
            case PHONE_NUMBER -> settingsService.changePhoneNumber(null, user, true);
            case SHIPPING_ADDRESS -> settingsService.changeShippingAddress(null, user, true);
            default -> throw new IllegalStateException("Couldn't clear settings!");
        };
    }

    @PostMapping("/delete-account")
    public ModelAndView deleteAccount(@AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.deleteAccount(user);
    }
}