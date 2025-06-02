package com.mercatto.sales.common.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class CommonResponse {
    private UUID id;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
