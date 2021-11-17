package com.mirrors.mirrorsbackend.mvc.settings.request;

import com.mirrors.mirrorsbackend.mvc.settings.CountryEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ShippingAddressRequest {
    private final CountryEnum country;
    private final String city;
    private final String streetAddress;
    private final String zipCode;
}
