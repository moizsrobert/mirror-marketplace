package com.mirrors.mirrorsbackend.page.settings;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.page.settings.request.PasswordRequest;
import com.mirrors.mirrorsbackend.page.settings.request.PersonalInfoRequest;
import com.mirrors.mirrorsbackend.page.settings.request.PhoneNumberRequest;
import com.mirrors.mirrorsbackend.page.settings.request.ShippingAddressRequest;
import com.mirrors.mirrorsbackend.utils.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class SettingsService {

    private MarketplaceUserRepository marketplaceUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PasswordValidator passwordValidator;

    public ModelAndView changePersonalInfo(PersonalInfoRequest request, MarketplaceUser user) {
        testLength(request.getDisplayName(), 3, 32, "Display name");
        testLength(request.getFirstName(), 0, 128, "First name");
        testLength(request.getFirstName(), 0, 64, "Last name");

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

    public ModelAndView changePhoneNumber(PhoneNumberRequest request, MarketplaceUser user) {
        String phoneNumber = request.getPhoneNumber();

        if (phoneNumber.length() != 0) {
            if (request.getPhoneNumber().toCharArray()[0] == '+')
                phoneNumber = phoneNumber.substring(1);
            for (char c : phoneNumber.toCharArray())
                if (!Character.isDigit(c))
                    throw new IllegalStateException("Phone number can only contain numeric characters!");

            testLength(phoneNumber, 8, 15, "Phone number");
        }

        marketplaceUserRepository.changePhoneNumber(
                phoneNumber,
                user.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }

    public ModelAndView changeShippingAddress(ShippingAddressRequest request, MarketplaceUser user) {
        if (Arrays.stream(CountryEnum.values()).noneMatch((s) -> s.name().equals(request.getCountry().name())))
            throw new IllegalStateException("Country doesn't exist!");

        testLength(request.getCity(), 0, 85, "City");

        testLength(request.getStreetAddress(), 0, 128, "Street address");
        if (request.getStreetAddress().split(" ").length < 3  && request.getStreetAddress().length() != 0)
            throw new IllegalStateException("Invalid street address!");

        testLength(request.getZipCode(), 0, 7, "Zip code");

        marketplaceUserRepository.changeShippingAddress(
                request.getCountry(),
                request.getCity(),
                request.getStreetAddress(),
                request.getZipCode(),
                user.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }

    private void testLength(String string, int min, int max, String errorField) {
        int l = string.length();
        if (l < min) throw new IllegalStateException(errorField + " can't be shorter than " + min + " characters!");
        if (l > max) throw new IllegalStateException(errorField + " can't be longer than " + max + " characters!");
    }
}
