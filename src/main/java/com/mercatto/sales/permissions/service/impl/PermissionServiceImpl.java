package com.mercatto.sales.permissions.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.modules.dto.response.ModuleDto;
import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.modules.repository.ModulesRepository;
import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;
import com.mercatto.sales.permissions.entity.PermissionEntity;
import com.mercatto.sales.permissions.entity.PermissionId;
import com.mercatto.sales.permissions.repository.PermissionRepository;
import com.mercatto.sales.permissions.service.PermissionService;
import com.mercatto.sales.permissions.specification.PermissionSpecification;
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
    private CompanyRepository companyRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public PermissionResponse save(String companyId, PermissionRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
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
        permission.setCompany(company);

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
    public PermissionResponse findOne(String companyId, String profileId, String moduleId) {
        throw new UnsupportedOperationException("Unimplemented method 'findOne'");
    }

    @Override
    public PermissionResponse update(String companyId, String profileId, String moduleId, PermissionRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String companyId, String profileId, String moduleId) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<PermissionResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        return permissionRepository.findAll(filterWithParameters(paramsNew))
                .stream().map(this::mapPermission).toList();
    }

    public Specification<PermissionEntity> filterWithParameters(Map<String, String> parameters) {
        return new PermissionSpecification().getSpecificationByFilters(parameters);
    }

}
