package com.mirrors.mirrorsbackend.mvc.profile.request;

import com.mirrors.mirrorsbackend.mvc.profile.ProfileSettingEnum;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SettingRequest {
    private ProfileSettingEnum valueEnum;
}
