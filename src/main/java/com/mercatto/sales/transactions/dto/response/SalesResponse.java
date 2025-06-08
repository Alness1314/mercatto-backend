package com.mercatto.sales.transactions.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
public class SalesResponse {
    private UUID id;
    private LocalDateTime transactionDateTime;
    private BigDecimal amount;
    private String paymentMethod;
    private Boolean sync;
    private UserDto user;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
