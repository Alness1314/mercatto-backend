package com.mercatto.sales.products.dto.response;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.categories.dto.response.CategoryResponse;
import com.mercatto.sales.unit.dto.response.UnitMeasurementResp;

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
public class ProductResponse {
    private UUID id;
    private String name;
    private String code;
    private BigDecimal price;
    private BigInteger stock;
    private CategoryResponse category;
    private UnitMeasurementResp unit;
    private Boolean active;
    private BigDecimal tax;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
