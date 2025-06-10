package com.mercatto.sales.country.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.country.dto.request.CountryRequest;
import com.mercatto.sales.country.dto.response.CountryResponse;
import com.mercatto.sales.common.model.ResponseServerDto;

public interface CountryService {
    public List<CountryResponse> find(Map<String, String> parameters);
    public CountryResponse findOne(String id);
    public CountryResponse save(CountryRequest request);
    public ResponseServerDto multiSaving(List<CountryRequest> countryList);
}
