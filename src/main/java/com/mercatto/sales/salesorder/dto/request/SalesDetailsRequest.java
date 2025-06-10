package com.mercatto.sales.salesorder.dto.request;

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
public class SalesDetailsRequest {
    private String salesId;
    private String productId;
    private BigInteger stock;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private BigDecimal tax;
}
