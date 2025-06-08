package com.mercatto.sales.products.dto.request;

import java.math.BigDecimal;
import java.math.BigInteger;

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
public class ProductRequest {
    private String name;
    private String code;
    private BigDecimal price;
    private BigInteger stock;
    private String categoryId;
    private String unitId;
    private Boolean active;
    private BigDecimal tax;
}
