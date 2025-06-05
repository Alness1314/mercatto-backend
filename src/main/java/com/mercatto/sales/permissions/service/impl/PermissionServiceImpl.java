package com.mercatto.sales.permissions.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercatto.sales.modules.dto.response.ModuleDto;
import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.modules.repository.ModulesRepository;
import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;
import com.mercatto.sales.permissions.entity.PermissionEntity;
import com.mercatto.sales.permissions.entity.PermissionId;
import com.mercatto.sales.permissions.repository.PermissionRepository;
import com.mercatto.sales.permissions.service.PermissionService;
import com.mercatto.sales.profiles.dto.response.ProfileDto;
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
    public PermissionResponse save(PermissionRequest request) {
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

        return mapPermission(permissionRepository.save(permission));
    }

    private PermissionResponse mapPermission(PermissionEntity source) {
        PermissionResponse permission = new PermissionResponse();
        ProfileDto profile = new ProfileDto();
        profile.setId(source.getProfile().getId().toString());
        profile.setName(source.getProfile().getName());
        permission.setProfile(profile);
        ModuleDto module = new ModuleDto();
        module.setId(source.getModule().getId().toString());
        module.setName(source.getModule().getName());
        permission.setModule(module);
        permission.setCanCreate(source.isCanCreate());
        permission.setCanRead(source.isCanRead());
        permission.setCanUpdate(source.isCanUpdate());
        permission.setCanDelete(source.isCanDelete());
        return permission;
    }

    @Override
    public PermissionResponse findOne(String profileId, String moduleId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOne'");
    }

    @Override
    public PermissionResponse update(String profileId, String moduleId, PermissionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String profileId, String moduleId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
