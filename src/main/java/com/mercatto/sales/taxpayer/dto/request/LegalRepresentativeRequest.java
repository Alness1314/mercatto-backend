package com.mercatto.sales.taxpayer.dto.request;

import com.mercatto.sales.annotations.build.IsRFC;

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
public class LegalRepresentativeRequest {
    
    @NotNull(message = "Debe contener el nombre completo")
    private String fullName;

    @IsRFC
    @NotNull(message = "El RFC no puede estar vacío")
    private String rfc;
}
