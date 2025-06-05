package com.mercatto.sales.permissions.service;

import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;

public interface PermissionService {
    public PermissionResponse save(PermissionRequest request);
    public PermissionResponse findOne(String profileId, String moduleId);
    public PermissionResponse update(String profileId, String moduleId, PermissionRequest request);
    public void delete(String profileId, String moduleId);
    
}
