package com.mirrors.mirrorsbackend.mvc.settings;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.mvc.settings.request.PasswordRequest;
import com.mirrors.mirrorsbackend.mvc.settings.request.PersonalInfoRequest;
import com.mirrors.mirrorsbackend.mvc.settings.request.PhoneNumberRequest;
import com.mirrors.mirrorsbackend.mvc.settings.request.ShippingAddressRequest;
import com.mirrors.mirrorsbackend.utils.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class SettingsService {

    private MarketplaceUserRepository marketplaceUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PasswordValidator passwordValidator;

    public ModelAndView changePersonalInfo(PersonalInfoRequest request, MarketplaceUser user) {
        if (!request.getDisplayName().matches("[a-zA-Z0-9].{3,32}$"))
            throw new IllegalStateException("Invalid display name!");

        if (!request.getFirstName().matches("[A-Za-z].{0,128}$"))
            throw new IllegalStateException("Invalid first name!");

        if (!request.getLastName().matches("[A-Za-z].{0,64}$"))
            throw new IllegalStateException("Invalid last name!");

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

            if (!phoneNumber.matches("[^0-9]*.{8,15}$"))
                throw new IllegalStateException("Invalid phone number");
        }

        marketplaceUserRepository.changePhoneNumber(
                phoneNumber,
                user.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }

    public ModelAndView changeShippingAddress(ShippingAddressRequest request, MarketplaceUser user) {
        if (Arrays.stream(CountryEnum.values()).noneMatch((s) -> s.name().equals(request.getCountry().name())))
            throw new IllegalStateException("Country doesn't exist!");


        if (!request.getCity().matches("[A-Za-z-]*.{0,85}$"))
            throw new IllegalStateException("Invalid city name!");

        if (!request.getStreetAddress().matches("[^A-Za-z0-9.-]*.{0,128}$"))
            throw new IllegalStateException("Invalid street address");

        if (request.getStreetAddress().split(" ").length < 3  && request.getStreetAddress().length() != 0)
            throw new IllegalStateException("Invalid street address!");

        if (!request.getZipCode().matches("[A-Z0-9]*.{0,7}$"))
            throw new IllegalStateException("Invalid zip code!");

        marketplaceUserRepository.changeShippingAddress(
                request.getCountry(),
                request.getCity(),
                request.getStreetAddress(),
                request.getZipCode(),
                user.getId());
        return new ModelAndView("redirect:/settings?settings_saved");
    }
}
