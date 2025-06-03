package com.mercatto.sales.country.dto.response;

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
public class CountryResponse extends CommonResponse {
    private String name;
    private String code;
}
