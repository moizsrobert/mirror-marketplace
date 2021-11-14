package com.mirrors.mirrorsbackend.page.settings.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PhoneNumberRequest {
    private String phoneNumber;
}
