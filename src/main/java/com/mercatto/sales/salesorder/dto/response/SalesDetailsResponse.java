package com.mercatto.sales.salesorder.dto.response;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.products.dto.response.ProductResponse;
import com.mercatto.sales.transactions.dto.response.SalesResponse;

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
public class SalesDetailsResponse {
    private UUID id;
    private SalesResponse sales;
    private ProductResponse product;
    private BigInteger stock;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
