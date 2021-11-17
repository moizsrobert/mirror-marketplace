package com.mirrors.mirrorsbackend.marketplacepost;

import lombok.Getter;

public enum CategoryEnum {
    CLASSIFIEDS("Apróhirdetések"),
    CLOTHING_AND_ACCESSORIES("Ruhák és kiegészítők"),
    ELECTRONICS("Elektronika"),
    ENTERTAINMENT("Szórakozás"),
    FAMILY("Család"),
    HOBBIES("Hobbik"),
    HOME_AND_GARDEN("Otthon és kert"),
    HOUSING("Háztartás"),
    VEHICLES("Járművek"),
    MISC("Egyéb");

    CategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    @Getter
    private final String displayName;
}
