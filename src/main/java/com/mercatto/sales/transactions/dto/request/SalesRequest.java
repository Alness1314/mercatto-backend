package com.mercatto.sales.transactions.dto.request;

import java.math.BigDecimal;

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
public class SalesRequest {
    private String transactionDateTime;
    private BigDecimal amount;
    private String paymentMethod;
    private Boolean sync;
    private String userId;
}
