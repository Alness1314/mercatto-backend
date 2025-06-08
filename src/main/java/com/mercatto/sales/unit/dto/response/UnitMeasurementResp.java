package com.mercatto.sales.unit.dto.response;

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
public class UnitMeasurementResp {
    private UUID id;
    private String name;
    private String abbreviation;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
