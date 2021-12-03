package com.mirrors.mirrorsbackend.controller.profile.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DeleteRequest extends SettingRequest {
    private final String password;
}
