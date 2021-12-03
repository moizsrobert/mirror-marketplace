package com.mirrors.mirrorsbackend.controller.profile.request;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
public class PersonalInfoRequest extends SettingRequest {
    private final String displayName;
    private final String firstName;
    private final String lastName;
}
