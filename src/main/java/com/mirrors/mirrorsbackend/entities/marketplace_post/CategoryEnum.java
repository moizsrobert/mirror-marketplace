package com.mirrors.mirrorsbackend.entities.marketplace_post;

import lombok.Getter;

public enum CategoryEnum {
    NOTHING_SELECTED("Select a category..."),
    CLASSIFIEDS("Classifieds"),
    CLOTHING_AND_ACCESSORIES("Clothing and Accessories"),
    ELECTRONICS("Electronics"),
    ENTERTAINMENT("Entertainment"),
    FAMILY("Family"),
    HOBBIES("Hobbies"),
    HOME_AND_GARDEN("Home and Design"),
    HOUSING("Housing"),
    VEHICLES("Vehicles"),
    MISC("Miscellaneous");

    CategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    @Getter
    private final String displayName;
}
