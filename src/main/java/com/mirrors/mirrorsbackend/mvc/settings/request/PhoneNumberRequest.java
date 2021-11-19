package com.mirrors.mirrorsbackend.mvc.settings.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PhoneNumberRequest extends SettingRequest{
    private String phoneNumber;
}
