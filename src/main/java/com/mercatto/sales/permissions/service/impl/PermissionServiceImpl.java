package com.mercatto.sales.permissions.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.modules.repository.ModulesRepository;
import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.entity.PermissionEntity;
import com.mercatto.sales.permissions.entity.PermissionId;
import com.mercatto.sales.permissions.repository.PermissionRepository;
import com.mercatto.sales.permissions.service.PermissionService;
import com.mercatto.sales.profiles.entity.ProfileEntity;
import com.mercatto.sales.profiles.repository.ProfileRepository;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ModulesRepository modulesRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public PermissionEntity save(PermissionRequest request) {
        PermissionEntity permission = new PermissionEntity();

        // Asignar clave compuesta
        PermissionId id = new PermissionId();
        id.setProfileId(UUID.fromString(request.getProfileId()));
        id.setModuleId(UUID.fromString(request.getModuleId()));
        permission.setId(id);

        // Asignar entidades relacionadas (referencia mínima)
        ProfileEntity profile = profileRepository.findById(UUID.fromString(request.getProfileId()))
                .orElseThrow(
                        () -> new IllegalArgumentException("Profile not found with id: " + request.getProfileId()));
        permission.setProfile(profile);

        ModulesEntity module = modulesRepository.findById(UUID.fromString(request.getModuleId()))
                .orElseThrow(() -> new IllegalArgumentException("Module not found with id: " + request.getModuleId()));
        permission.setModule(module);

        // Asignar flags de permisos
        permission.setCanCreate(request.isCanCreate());
        permission.setCanRead(request.isCanRead());
        permission.setCanUpdate(request.isCanUpdate());
        permission.setCanDelete(request.isCanDelete());

        return permissionRepository.save(permission);
    }

}
