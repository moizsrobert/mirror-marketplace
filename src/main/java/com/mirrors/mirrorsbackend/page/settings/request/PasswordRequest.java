package com.mirrors.mirrorsbackend.page.settings.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PasswordRequest {
    private final String oldPassword;
    private final String password;
    private final String confirmPassword;
}
