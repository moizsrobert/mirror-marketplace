package com.mirrors.mirrorsbackend.mvc.settings;

import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostRepository;
import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostService;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.mvc.settings.request.PasswordRequest;
import com.mirrors.mirrorsbackend.mvc.settings.request.PersonalInfoRequest;
import com.mirrors.mirrorsbackend.mvc.settings.request.PhoneNumberRequest;
import com.mirrors.mirrorsbackend.mvc.settings.request.ShippingAddressRequest;
import com.mirrors.mirrorsbackend.utils.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import java.util.Arrays;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SettingsService {

    final private MarketplaceUserRepository marketplaceUserRepository;
    final private MarketplacePostRepository marketplacePostRepository;
    final private MarketplaceUserService marketplaceUserService;
    final private MarketplacePostService marketplacePostService;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;
    final private PasswordValidator passwordValidator;

    public void loadSettingsPage(@AuthenticationPrincipal MarketplaceUser user, Model model) {
        Optional<MarketplaceUser> optionalUser = marketplaceUserRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            model.addAttribute("user", user);
            model.addAttribute("posts", marketplacePostRepository.findAllByUser(user));
        }
        else throw new IllegalStateException("Couldn't load settings!");
    }

    public ModelAndView changePersonalInfo(PersonalInfoRequest request, MarketplaceUser user, boolean clear) {
        if (!clear) {
            if (!request.getDisplayName().matches("[a-zA-Z_0-9].{3,32}"))
                throw new IllegalStateException("Invalid display name!");

            if (!request.getFirstName().matches("\\p{IsAlphabetic}{2,25}"))
                throw new IllegalStateException("Invalid first name!");

            if (!request.getLastName().matches("\\p{IsAlphabetic}{2,25}"))
                throw new IllegalStateException("Invalid last name!");
        } else {
            request = new PersonalInfoRequest(
                    user.getDisplayName(),
                    "",
                    ""
            );
        }

        marketplaceUserRepository.changePersonalInfo(
                request.getDisplayName(),
                request.getFirstName(),
                request.getLastName(),
                user.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }

    public ModelAndView changePassword(PasswordRequest request, MarketplaceUser user) {
        if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new IllegalStateException("Old password is wrong!");

        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new IllegalStateException("Passwords don't match!");

        boolean isValidPassword = passwordValidator.test(request.getPassword());
        if (!isValidPassword)
            throw new IllegalStateException("Password must contain " +
                    "atleast 1 uppercase letter, " +
                    "1 lowercase letter, " +
                    "1 number " +
                    "and 8 characters");

        String newPassword = bCryptPasswordEncoder.encode(request.getPassword());

        marketplaceUserRepository.changePassword(
                newPassword,
                user.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }

    public ModelAndView changePhoneNumber(PhoneNumberRequest request, MarketplaceUser user, boolean clear) {
        if (!clear) {
            if (!request.getPhoneNumber().equals("") && request.getPhoneNumber().toCharArray()[0] == '+')
                request.setPhoneNumber(request.getPhoneNumber().substring(1));
            if (!request.getPhoneNumber().matches("\\p{Digit}{8,15}"))
                throw new IllegalStateException("Invalid phone number");
        } else {
            request = new PhoneNumberRequest("");
        }

        marketplaceUserRepository.changePhoneNumber(
                request.getPhoneNumber(),
                user.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }

    public ModelAndView changeShippingAddress(ShippingAddressRequest request, MarketplaceUser user, boolean clear) {
        if (!clear) {
            ShippingAddressRequest finalRequest = request;
            if (Arrays.stream(CountryEnum.values()).noneMatch((s) -> s.name().equals(finalRequest.getCountry().name())))
                throw new IllegalStateException("Country doesn't exist!");

            if (!request.getCity().matches("[-\\p{IsAlphabetic}]{2,85}"))
                throw new IllegalStateException("Invalid city name!");

            if (!request.getStreetAddress().matches("[-.\s\\p{IsAlnum}]{6,60}"))
                throw new IllegalStateException("Invalid street address");

            int indexOfLastPart = request.getStreetAddress().split(" ").length - 1;
            String addressSecondPart = request.getStreetAddress().split(" ")[indexOfLastPart];
            String addressFirstPart = request.getStreetAddress().replace(addressSecondPart, "");

            if (!addressFirstPart.matches("[-\s\\p{IsAlphabetic}]"))
                throw new IllegalStateException("Invalid street address!");

            if (!addressSecondPart.matches("[.\\p{IsDigit}]"))
                throw new IllegalStateException("Invalid street address!");

            if (request.getStreetAddress().split(" ").length < 3  && request.getStreetAddress().length() != 0)
                throw new IllegalStateException("Invalid street address!");

            if (!request.getZipCode().matches("\\p{Alnum}{4,8}"))
                throw new IllegalStateException("Invalid zip code!");
        } else {
            request = new ShippingAddressRequest(
                    CountryEnum.NOTHING_SELECTED,
                    "",
                    "",
                    ""
            );
        }

        marketplaceUserRepository.changeShippingAddress(
                request.getCountry(),
                request.getCity(),
                request.getStreetAddress(),
                request.getZipCode(),
                user.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }

    public ModelAndView deleteAccount(MarketplaceUser user) {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
        marketplacePostService.deleteMarketplacePostsOfUser(user);
        marketplaceUserService.deleteMarketplaceUser(user);
        return new ModelAndView("redirect:api/login");
    }
}