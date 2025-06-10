package com.mercatto.sales.company.dto.request;

import com.mercatto.sales.address.dto.request.AddressRequest;
import com.mercatto.sales.annotations.build.IsUUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class CompanyRequest {
    @NotNull
    private String name;

    @NotNull
    private String description;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String phone;

    @IsUUID
    private String imageId;

    @Valid
    private AddressRequest address;
    
    @IsUUID
    @NotNull
    private String taxpayerId;
}
