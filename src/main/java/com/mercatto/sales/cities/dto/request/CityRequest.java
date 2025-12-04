package com.mercatto.sales.cities.dto.request;

import com.mercatto.sales.annotations.build.IsUUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CityRequest {
    @NotNull
    @Size(min = 1, max = 64)
    private String name;

    @NotNull
    @IsUUID
    private String stateId;
}
