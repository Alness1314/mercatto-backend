package com.mercatto.sales.taxpayer.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.address.dto.response.AddressResponse;

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
public class TaxpayerResponse {
    private UUID id;
    private String rfc;
    private String typePerson;
    private String corporateReasonOrNaturalPerson;
    private LegalRepresentativeResponse legalRepresentative;
    private AddressResponse address;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
