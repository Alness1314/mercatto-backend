package com.mercatto.sales.profiles.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.profiles.dto.request.ProfileRequest;
import com.mercatto.sales.profiles.dto.response.ProfileResponse;

public interface ProfileService {
    public ProfileResponse save(ProfileRequest request);
    public ProfileResponse findOne(String id);
    public ProfileResponse findByName(String name);
    public List<ProfileResponse> find(Map<String, String> params);
    public ProfileResponse update(String id, ProfileRequest request);
    public ResponseServerDto delete(String id);
    //public List<ModuleResponse> getModulesByProfile(String profileId);
    public ResponseServerDto multiSaving(List<ProfileRequest> profileList);
}
