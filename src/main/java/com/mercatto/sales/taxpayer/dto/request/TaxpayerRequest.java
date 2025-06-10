package com.mercatto.sales.taxpayer.dto.request;

import com.mercatto.sales.address.dto.request.AddressRequest;
import com.mercatto.sales.annotations.build.IsRFC;
import com.mercatto.sales.annotations.build.ValidEnum;
import com.mercatto.sales.common.enums.TypePerson;

import jakarta.validation.Valid;
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
public class TaxpayerRequest {
    @NotNull
    private String corporateReasonOrNaturalPerson;
    
    @IsRFC
    @NotNull
    private String rfc;
    
    @ValidEnum(enumClass = TypePerson.class, field = "name", message = "los valores permitidos son Física y Moral")
    @NotNull
    private String typePerson;

    @Valid
    private LegalRepresentativeRequest legalRepresentative;
    
    @Valid
    @NotNull
    private AddressRequest address;
}
