package com.mirrors.mirrorsbackend.mvc.profile.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DeleteRequest extends SettingRequest {
    private final String password;
}
