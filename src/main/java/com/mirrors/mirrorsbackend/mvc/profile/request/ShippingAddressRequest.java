package com.mirrors.mirrorsbackend.mvc.profile.request;

import com.mirrors.mirrorsbackend.mvc.profile.CountryEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ShippingAddressRequest extends SettingRequest{
    private final CountryEnum country;
    private final String city;
}
