package com.mercatto.sales.profiles.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.profiles.dto.request.ProfileRequest;
import com.mercatto.sales.profiles.dto.response.ProfileResponse;

public interface ProfileService {
    public ProfileResponse save(String companyId, ProfileRequest request);
    public ProfileResponse findOne(String companyId, String id);
    public ProfileResponse findByName(String name);
    public List<ProfileResponse> find(String companyId, Map<String, String> params);
    public ProfileResponse update(String companyId, String id, ProfileRequest request);
    public ResponseServerDto delete(String companyId, String id);
    public ResponseServerDto multiSaving(String companyId, List<ProfileRequest> profileList);
}
