package com.mercatto.sales.settings.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class SettingsResponse {
    private UUID id;
    private String key;
    private String value;
    private String dataType;
    private String grouping;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
