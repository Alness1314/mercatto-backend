package com.mercatto.sales.taxpayer.dto.request;

import com.mercatto.sales.address.dto.request.AddressRequest;

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
    private String corporateReasonOrNaturalPerson;
    private String rfc;
    private String typePerson;
    private LegalRepresentativeRequest legalRepresentative;
    private AddressRequest address;
}
