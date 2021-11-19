package com.mirrors.mirrorsbackend.mvc.settings.request;

import com.mirrors.mirrorsbackend.mvc.settings.SettingValueEnum;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SettingRequest {
    private SettingValueEnum valueEnum;
}
