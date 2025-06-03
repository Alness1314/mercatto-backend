package com.mercatto.sales.cities.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.cities.dto.request.CityRequest;
import com.mercatto.sales.cities.dto.response.CityResponse;

public interface CityService {
    public List<CityResponse> find(Map<String, String> parameters);
    public CityResponse findOne(String id);
    public CityResponse save(CityRequest request);
    public ResponseServerDto multiSaving(String stateId, List<String> citiesList);
    public CityResponse update(String id, CityRequest request);
    public ResponseServerDto delete(String id);
}
