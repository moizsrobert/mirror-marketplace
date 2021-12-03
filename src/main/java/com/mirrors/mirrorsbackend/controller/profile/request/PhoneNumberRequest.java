package com.mirrors.mirrorsbackend.controller.profile.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PhoneNumberRequest extends SettingRequest{
    private String phoneNumber;
}
