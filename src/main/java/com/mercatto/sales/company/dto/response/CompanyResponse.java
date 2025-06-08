package com.mercatto.sales.company.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.address.dto.response.AddressResponse;
import com.mercatto.sales.files.dto.FileResponse;
import com.mercatto.sales.taxpayer.dto.response.TaxpayerResponse;

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
public class CompanyResponse {
    private UUID id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private AddressResponse address;
    private FileResponse image;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
    private TaxpayerResponse taxpayer;
}
