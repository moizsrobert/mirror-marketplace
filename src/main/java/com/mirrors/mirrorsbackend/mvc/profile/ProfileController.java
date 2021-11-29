package com.mirrors.mirrorsbackend.mvc.profile;

import com.mirrors.mirrorsbackend.mvc.profile.request.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@AllArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/personal")
    public void savePersonalInfo(PersonalInfoRequest request) {
        profileService.changePersonalInfo(request, false);
    }

    @PostMapping("/password")
    public void saveNewPassword(PasswordRequest request) {
        profileService.changePassword(request);
    }

    @PostMapping("/phone")
    public void savePhoneNumber(PhoneNumberRequest request) {
        profileService.changePhoneNumber(request, false);
    }

    @PostMapping("/address")
    public void saveShippingAddress(ShippingAddressRequest request) {
        profileService.changeShippingAddress(request, false);
    }

    @PostMapping("/clear")
    public void clearSettings(ProfileSettingEnum value) {
        if (Arrays.stream(ProfileSettingEnum.values()).noneMatch((v) -> v.equals(value)))
            throw new IllegalStateException("Couldn't clear settings!");
        switch (value) {
            case PERSONAL_INFO -> profileService.changePersonalInfo(null, true);
            case PHONE_NUMBER -> profileService.changePhoneNumber(null, true);
            case SHIPPING_ADDRESS -> profileService.changeShippingAddress(null, true);
            default -> throw new IllegalStateException("Couldn't clear settings!");
        }
    }

    @PostMapping("/delete-account")
    public void deleteAccount(DeleteRequest deleteRequest) {
        profileService.deleteAccount(deleteRequest);
    }
}