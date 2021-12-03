package com.mirrors.mirrorsbackend.controller.password_reset;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PasswordResetRequest {
    private final String password;
    private final String confirmPassword;
    private final String token;
}
