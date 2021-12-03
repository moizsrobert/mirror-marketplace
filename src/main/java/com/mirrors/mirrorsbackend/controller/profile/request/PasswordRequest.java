package com.mirrors.mirrorsbackend.controller.profile.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PasswordRequest extends SettingRequest {
    private final String oldPassword;
    private final String password;
    private final String confirmPassword;
}
