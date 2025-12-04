package com.mercatto.sales.company.dto.request;

import com.mercatto.sales.address.dto.request.AddressRequest;
import com.mercatto.sales.annotations.build.IsNumberString;
import com.mercatto.sales.annotations.build.IsUUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 3, max = 128)
    private String name;

    @NotNull
    @Size(min = 10, max = 256)
    private String description;

    @Email
    @NotNull
    @Size(min = 1, max = 32)
    private String email;

    @NotNull
    @IsNumberString
    @Size(min = 1, max = 20)
    private String phone;

    @IsUUID
    private String imageId;

    @Valid
    private AddressRequest address;

    @IsUUID
    @NotNull
    private String taxpayerId;
}
