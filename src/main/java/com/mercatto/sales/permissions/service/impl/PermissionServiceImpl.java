package com.mercatto.sales.permissions.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.modules.dto.response.ModuleDto;
import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.modules.repository.ModulesRepository;
import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.request.PermissionUpdateReq;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;
import com.mercatto.sales.permissions.entity.PermissionEntity;
import com.mercatto.sales.permissions.entity.PermissionId;
import com.mercatto.sales.permissions.repository.PermissionRepository;
import com.mercatto.sales.permissions.service.PermissionService;
import com.mercatto.sales.permissions.specification.PermissionSpecification;
import com.mercatto.sales.profiles.dto.response.ProfileDto;
import com.mercatto.sales.profiles.entity.ProfileEntity;
import com.mercatto.sales.profiles.repository.ProfileRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));
        PermissionEntity permission = new PermissionEntity();

        // Asignar clave compuesta
        PermissionId id = new PermissionId();
        id.setProfileId(UUID.fromString(request.getProfileId()));
        id.setModuleId(UUID.fromString(request.getModuleId()));
        permission.setId(id);

        // Asignar entidades relacionadas (referencia mínima)
        ProfileEntity profile = profileRepository.findById(UUID.fromString(request.getProfileId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getProfileId())));
        permission.setProfile(profile);

        ModulesEntity module = modulesRepository.findById(UUID.fromString(request.getModuleId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getModuleId())));
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
        Map<String, String> params = Map.of(Filters.KEY_PROFILE, profileId, Filters.KEY_MODULE, moduleId, Filters.KEY_COMPANY_ID, companyId);
        PermissionEntity permissionEntity = permissionRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, moduleId)));
        return mapPermission(permissionEntity);
    }

    @Override
    public PermissionResponse update(String companyId, String profileId, String moduleId, PermissionUpdateReq request) {
        // Verificar que la compañía existe
        companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));

        // Crear la clave compuesta para buscar el permiso existente
        PermissionId id = new PermissionId();
        id.setProfileId(UUID.fromString(profileId));
        id.setModuleId(UUID.fromString(moduleId));

        // Obtener el permiso existente
        PermissionEntity existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Permission not found for profile: " + profileId +
                                " and module: " + moduleId));

        // Verificar que el permiso pertenece a la compañía especificada
        if (!existingPermission.getCompany().getId().equals(UUID.fromString(companyId))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Permission does not belong to the specified company");
        }

        // Actualizar los flags de permisos (no se actualizan las relaciones que son
        // parte de la PK)
        existingPermission.setCanCreate(request.isCanCreate());
        existingPermission.setCanRead(request.isCanRead());
        existingPermission.setCanUpdate(request.isCanUpdate());
        existingPermission.setCanDelete(request.isCanDelete());

        // Guardar los cambios
        PermissionEntity updatedPermission = permissionRepository.save(existingPermission);

        return mapPermission(updatedPermission);
    }

    @Override
    public ResponseServerDto delete(String companyId, String profileId, String moduleId) {
        Map<String, String> params = Map.of("profle", profileId, Filters.KEY_MODULE, moduleId, Filters.KEY_COMPANY_ID, companyId);
        PermissionEntity permissionEntity = permissionRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "resource not found"));
        try {
            permissionRepository.delete(permissionEntity);
            return new ResponseServerDto(String.format(Messages.ENTITY_DELETE, moduleId), HttpStatus.ACCEPTED, true);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_DELETE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    String.format(Messages.ERROR_ENTITY_DELETE, e.getMessage()));
        }
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
