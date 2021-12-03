package com.mirrors.mirrorsbackend.controller.profile.request;

import com.mirrors.mirrorsbackend.controller.profile.ProfileSettingEnum;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SettingRequest {
    private ProfileSettingEnum valueEnum;
}
