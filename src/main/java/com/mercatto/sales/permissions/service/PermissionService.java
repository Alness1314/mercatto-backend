package com.mercatto.sales.permissions.service;

import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.entity.PermissionEntity;

public interface PermissionService {
    public PermissionEntity save(PermissionRequest request);
}
