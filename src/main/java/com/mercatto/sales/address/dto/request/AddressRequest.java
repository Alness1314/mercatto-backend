package com.mercatto.sales.address.dto.request;

import com.mercatto.sales.annotations.build.IsUUID;

import jakarta.annotation.Nullable;
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
public class AddressRequest {
    @Nullable
    @Size(min = 0, max = 32)
    private String nickname;

    @NotNull
    @Size(min = 1, max = 64)
    private String street;

    @NotNull
    @Size(min = 1, max = 15)
    private String number;

    @NotNull
    @Size(min = 1, max = 32)
    private String suburb;

    @NotNull
    @Size(min = 1, max = 64)
    private String zipCode;
    
    @Nullable
    @Size(min = 1, max = 128)
    private String reference;

    @NotNull
    @IsUUID
    private String countryId;
    
    @NotNull
    @IsUUID
    private String stateId;
    
    @NotNull
    @IsUUID
    private String cityId;
}
