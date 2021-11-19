package com.mirrors.mirrorsbackend.mvc.settings.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
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
