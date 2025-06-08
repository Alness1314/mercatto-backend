package com.mercatto.sales.taxpayer.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LegalRepresentativeResponse {
    private UUID id;
    private String fullName;
    private String rfc;
     private Boolean erased;
}
