package com.mercatto.sales.address.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.cities.dto.response.CityResponse;
import com.mercatto.sales.country.dto.response.CountryResponse;
import com.mercatto.sales.states.dto.response.StateResponse;

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
public class AddressResponse {
    private UUID id;
    private String nickname;
    private String street;
    private String number;
    private String suburb;
    private String zipCode;
    private String reference;
    private CountryResponse country;
    private StateResponse state;
    private CityResponse city;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
