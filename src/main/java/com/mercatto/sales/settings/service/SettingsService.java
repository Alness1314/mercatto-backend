package com.mercatto.sales.settings.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.settings.dto.request.SettingsRequest;
import com.mercatto.sales.settings.dto.response.SettingsResponse;

public interface SettingsService {
    public List<SettingsResponse> find(String companyId, Map<String, String> params);
    public SettingsResponse findOne(String companyId, String id);
    public SettingsResponse save(String companyId, SettingsRequest request);
    public SettingsResponse update(String companyId, String id, SettingsRequest request);
    public ResponseServerDto delete(String companyId, String id);
}
