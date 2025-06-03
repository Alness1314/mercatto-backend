package com.mercatto.sales.cities.dto.response;

import com.mercatto.sales.common.model.dto.CommonResponse;

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
public class CityResponse extends CommonResponse {
    private String name;
}
