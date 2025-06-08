package com.mercatto.sales.settings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsRequest {
    private String key;
    private String value;
    private String dataType;
    private String grouping;
}
