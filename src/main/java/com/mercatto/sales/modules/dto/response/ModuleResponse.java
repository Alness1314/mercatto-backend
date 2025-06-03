package com.mercatto.sales.modules.dto.response;

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
public class ModuleResponse extends CommonResponse{
    private String name;
    private String route;
    private String iconName;
    private Boolean read;
    private Boolean write;
    private Boolean delete;
}
