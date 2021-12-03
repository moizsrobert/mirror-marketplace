package com.mirrors.mirrorsbackend.controller.profile;

import com.mirrors.mirrorsbackend.model.marketplace_post.MarketplacePostService;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserRepository;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.model.saved_post.SavedPostService;
import com.mirrors.mirrorsbackend.exception.MarketplaceException;
import com.mirrors.mirrorsbackend.controller.profile.request.*;
import com.mirrors.mirrorsbackend.util.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class ProfileService {

    final private MarketplaceUserRepository marketplaceUserRepository;
    final private MarketplaceUserService marketplaceUserService;
    final private MarketplacePostService marketplacePostService;
    final private SavedPostService savedPostService;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;
    final private PasswordValidator passwordValidator;

    public void changePersonalInfo(PersonalInfoRequest request, boolean clear) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();

        if (!clear) {
            if (!request.getDisplayName().matches("[.\\p{Alnum}]{3,32}"))
                throw new MarketplaceException("Invalid display name!");

            if (!request.getFirstName().matches("\\p{IsAlphabetic}{2,25}"))
                throw new MarketplaceException("Invalid first name!");

            if (!request.getLastName().matches("\\p{IsAlphabetic}{2,25}"))
                throw new MarketplaceException("Invalid last name!");
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
    }

    public void changePassword(PasswordRequest request) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();

        if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new MarketplaceException("Old password is wrong!");

        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new MarketplaceException("Passwords don't match!");

        boolean isValidPassword = passwordValidator.test(request.getPassword());
        if (!isValidPassword)
            throw new MarketplaceException("Password must contain " +
                    "atleast 1 uppercase letter, " +
                    "1 lowercase letter, " +
                    "1 number " +
                    "and 8 characters");

        String newPassword = bCryptPasswordEncoder.encode(request.getPassword());

        marketplaceUserRepository.changePassword(
                newPassword,
                user.getId());
    }

    public void changePhoneNumber(PhoneNumberRequest request, boolean clear) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();

        if (!clear) {
            if (!request.getPhoneNumber().equals("") && request.getPhoneNumber().toCharArray()[0] == '+')
                request.setPhoneNumber(request.getPhoneNumber().substring(1));
            if (!request.getPhoneNumber().matches("\\p{Digit}{8,15}"))
                throw new MarketplaceException("Invalid phone number");
        } else {
            request = new PhoneNumberRequest("");
        }

        marketplaceUserRepository.changePhoneNumber(
                request.getPhoneNumber(),
                user.getId());
    }

    public void changeShippingAddress(ShippingAddressRequest request, boolean clear) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();

        if (!clear) {
            ShippingAddressRequest finalRequest = request;
            if (Arrays.stream(CountryEnum.values()).noneMatch((s) -> s.name().equals(finalRequest.getCountry().name())))
                throw new MarketplaceException("Country doesn't exist!");

            if (request.getCountry() == CountryEnum.NOTHING_SELECTED)
                throw new MarketplaceException("You need to select a country!");

            if (!request.getCity().matches("[-\s\\p{IsAlphabetic}]{2,85}"))
                throw new MarketplaceException("Invalid city name!");
        } else {
            if (!marketplacePostService.getPostsOfUser(user).isEmpty())
                throw new MarketplaceException("You can't clear your address while having active posts!");

            request = new ShippingAddressRequest(
                    CountryEnum.NOTHING_SELECTED,
                    ""
            );
        }

        marketplaceUserRepository.changeShippingAddress(
                request.getCountry(),
                request.getCity().replaceAll("^ +| +$|( )+", "$1"),
                user.getId());
    }

    public void deleteAccount(DeleteRequest request) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new MarketplaceException("Incorrect password!");
        }

        savedPostService.deleteAllSavedPosts();
        marketplacePostService.deletePostsOfUser(user);
        marketplaceUserService.deleteUser(user);
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }
}