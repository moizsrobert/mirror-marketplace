package com.mirrors.mirrorsbackend.page.settings;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SettingsRequest {
    private final String displayName;
    private final String firstName;
    private final String lastName;
    private String phoneNumber;
    private final CountryEnum country;
    private final String city;
    private final String streetAddress;
    private final String zipCode;

    public void validate() {
        testLength(displayName, 3, 24, "Display name");

        testLength(firstName, 1, 128, "First name");

        testLength(lastName, 2, 64, "Last name");

        if (phoneNumber.toCharArray()[0] == '+') phoneNumber = phoneNumber.substring(1);
        for (char c : phoneNumber.toCharArray())
            if (!Character.isDigit(c)) throw new IllegalStateException("Phone number can only contain numeric characters!");
        testLength(phoneNumber, 8, 15, "Phone number");

        if (Arrays.stream(CountryEnum.values()).noneMatch((s) -> s.name().equals(country.name())))
            throw new IllegalStateException("Country doesn't exist!");

        testLength(city, 1, 85, "City");

        testLength(streetAddress, 5, 128, "Street address");
        if (streetAddress.split(" ").length < 3)
            throw new IllegalStateException("Invalid street address!");

        testLength(zipCode, 2, 7, "Zip code");
    }

    private void testLength(String string, int min, int max, String errorField) {
        int l = string.length();
        if (l == 0) throw new IllegalStateException(errorField + " can't be empty!");
        if (l < min) throw new IllegalStateException(errorField + " can't be shorten than " + min + " characters!");
        if (l > max) throw new IllegalStateException(errorField + " can't be longer than " + max + " characters!");
    }
}
