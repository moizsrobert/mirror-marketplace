package com.mirrors.mirrorsbackend.mvc.settings.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PhoneNumberRequest {
    private final String phoneNumber;
}
