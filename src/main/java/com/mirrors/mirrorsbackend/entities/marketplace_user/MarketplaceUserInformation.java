package com.mirrors.mirrorsbackend.entities.marketplace_user;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MarketplaceUserInformation {
    private final String email;
    private final String displayName;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String country;
    private final String city;

    public MarketplaceUserInformation(MarketplaceUser user) {
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.country = user.getCountry().getDisplayName();
        this.city = user.getCity();
    }
}
