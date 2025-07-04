package com.mercatto.sales.app.jwt;

import java.util.Map;

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
public class BodyDto {
    private String sub;
    private String iss;
    private Long exp;
    private Long iat;
    private Map<String, Object> claims;
}
