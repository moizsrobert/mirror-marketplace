package com.mirrors.mirrorsbackend.mvc.settings.request;

import com.mirrors.mirrorsbackend.mvc.settings.CountryEnum;
import lombok.*;

@Getter
@AllArgsConstructor
@ToString
public class ShippingAddressRequest extends SettingRequest{
    private final CountryEnum country;
    private final String city;
    private final String streetAddress;
    private final String zipCode;
}
