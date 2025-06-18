package com.mercatto.sales.permissions.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.request.PermissionUpdateReq;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;

public interface PermissionService {
    public PermissionResponse save(String companyId, PermissionRequest request);
    public List<PermissionResponse> find(String companyId, Map<String, String> params);
    public PermissionResponse findOne(String companyId, String profileId, String moduleId);
    public PermissionResponse update(String companyId, String profileId, String moduleId, PermissionUpdateReq request);
    public ResponseServerDto delete(String companyId, String profileId, String moduleId);

}
