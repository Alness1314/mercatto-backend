package com.mercatto.sales.permissions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.permissions.entity.PermissionEntity;
import com.mercatto.sales.permissions.entity.PermissionId;

public interface PermissionRepository extends JpaRepository<PermissionEntity, PermissionId>, JpaSpecificationExecutor<PermissionEntity> {
    
}
