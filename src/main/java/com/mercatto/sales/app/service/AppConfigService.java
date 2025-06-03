package com.mercatto.sales.app.service;

import java.util.List;

import com.mercatto.sales.common.model.ResponseServerDto;

import jakarta.servlet.http.HttpServletRequest;

public interface AppConfigService {
    public ResponseServerDto createDefaultCountries();
    public ResponseServerDto createDefaultStates();
    public List<ResponseServerDto> createDefaultCities();
    public ResponseServerDto createDefaultProfiles();
    public ResponseServerDto createDefaultUser();
    public ResponseServerDto checkStatusSession(HttpServletRequest request);
}
