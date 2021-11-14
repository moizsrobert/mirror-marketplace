package com.mirrors.mirrorsbackend.page.settings.request;

import com.mirrors.mirrorsbackend.page.settings.CountryEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ShippingAddressRequest {
    private CountryEnum country;
    private String city;
    private String streetAddress;
    private String zipCode;
}
