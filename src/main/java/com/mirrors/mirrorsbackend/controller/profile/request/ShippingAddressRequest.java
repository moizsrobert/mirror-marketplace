package com.mirrors.mirrorsbackend.controller.profile.request;

import com.mirrors.mirrorsbackend.controller.profile.CountryEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ShippingAddressRequest extends SettingRequest{
    private final CountryEnum country;
    private final String city;
}
