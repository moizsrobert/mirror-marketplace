package com.mirrors.mirrorsbackend.page.settings;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRepository;
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

    @GetMapping
    public ModelAndView settingsPage(@AuthenticationPrincipal MarketplaceUser user, Model model) {
        Optional<MarketplaceUser> optionalUser = marketplaceUserRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            model.addAttribute("user", user);
        } else throw new IllegalStateException("Couldn't load settings!");
        return new ModelAndView("/settings");
    }

    @PostMapping
    public ModelAndView saveSettings(SettingsRequest request, @AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.changeSettings(request, user);
    }

    @GetMapping("/reset_password")
    public ModelAndView resetPassword(@AuthenticationPrincipal MarketplaceUser user) {
        return settingsService.sendPasswordResetEmail(user);
    }
}
