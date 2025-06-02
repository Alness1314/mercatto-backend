package com.mercatto.sales.profiles.dto.response;

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
public class ProfileResponse extends CommonResponse{
    private String name;
}
