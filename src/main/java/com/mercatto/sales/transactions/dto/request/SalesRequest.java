package com.mercatto.sales.transactions.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mercatto.sales.users.dto.response.UserDto;

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
    private LocalDateTime transactionDateTime;
    private BigDecimal amount;
    private String paymentMethod;
    private Boolean sync;
    private UserDto userId;
}
