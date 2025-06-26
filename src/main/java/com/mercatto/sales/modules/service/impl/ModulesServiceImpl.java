package com.mercatto.sales.modules.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.modules.dto.request.ModuleRequest;
import com.mercatto.sales.modules.dto.response.ModuleResponse;
import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.modules.repository.ModulesRepository;
import com.mercatto.sales.modules.service.ModulesService;
import com.mercatto.sales.modules.specification.ModuleSpecification;
import com.mercatto.sales.permissions.dto.response.PermissionDto;
import com.mercatto.sales.permissions.entity.PermissionEntity;
import com.mercatto.sales.profiles.dto.response.ProfileDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModulesServiceImpl implements ModulesService {
    @Autowired
    private ModulesRepository modulesRepository;

    @Override
    public List<ModuleResponse> getFilteredSubmodules(String profileIdStr, String name) {
        if (name == null || profileIdStr == null) {
            return Collections.emptyList();
        }

        UUID profileId = UUID.fromString(profileIdStr);

        // 1. Buscar el módulo padre por nombre
        Optional<ModulesEntity> parentOpt = modulesRepository.findOne(ModuleSpecification.filterByName(name));
        if (parentOpt.isEmpty())
            return Collections.emptyList();

        ModulesEntity parent = parentOpt.get();

        // 2. Buscar hijos con permisos de ese perfil
        List<ModulesEntity> hijos = modulesRepository.findAll(
                ModuleSpecification.byParentIdAndProfile(parent.getId(), profileId));

        // 3. Limpiar los permisos que no son del perfil deseado
        hijos.forEach(hijo -> hijo.setPermissions(
                hijo.getPermissions().stream()
                        .filter(p -> p.getProfile().getId().equals(profileId))
                        .toList()));

        return hijos.stream().map(this::mapModule).toList();
    }

    @Override
    public ModuleResponse save(ModuleRequest request) {
        ModulesEntity module = new ModulesEntity();
        module.setName(request.getName());
        module.setRoute(request.getRoute());
        module.setIconName(request.getIconName());

        if (request.getParentId() != null) {
            ModulesEntity parent = modulesRepository.findById(UUID.fromString(request.getParentId()))
                    .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                            String.format(Messages.NOT_FOUND, request.getParentId())));
            module.setParent(parent);
        }

        return mapModule(modulesRepository.save(module));
    }

    @Override
    public ModuleResponse findOne(String id) {
        ModulesEntity module = modulesRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));

        return mapModule(module);
    }

    @Override
    public List<ModuleResponse> findAll(Map<String, String> params) {
        return modulesRepository.findAll(filterWithParameters(params))
                .stream()
                .map(this::mapModule)
                .toList();
    }

    @Override
    public ResponseServerDto multiSave(List<ModuleRequest> modules) {
        List<Map<String, Object>> response = new ArrayList<>();
        modules.forEach(item -> {
            ModuleResponse resp = save(item);
            if (resp != null) {
                response.add(Map.of(Filters.KEY_MODULE, resp.getName(), Filters.KEY_STATUS, true));
            } else {
                response.add(Map.of(Filters.KEY_MODULE, item.getName(), Filters.KEY_STATUS, false));
            }
        });
        return new ResponseServerDto("Modulos creados", HttpStatus.ACCEPTED, true, Map.of("data", response));
    }

    private PermissionDto mapPermission(PermissionEntity source) {
        PermissionDto permission = new PermissionDto();
        ProfileDto profile = new ProfileDto();
        profile.setId(source.getProfile().getId().toString());
        profile.setName(source.getProfile().getName());
        permission.setProfile(profile);
        permission.setCanCreate(source.isCanCreate());
        permission.setCanRead(source.isCanRead());
        permission.setCanUpdate(source.isCanUpdate());
        permission.setCanDelete(source.isCanDelete());
        return permission;
    }

    private ModuleResponse mapModule(ModulesEntity source) {
        ModuleResponse module = new ModuleResponse();
        module.setId(source.getId());
        module.setName(source.getName());
        module.setRoute(source.getRoute());
        module.setIconName(source.getIconName());
        if (source.getPermissions() == null) {
            module.setPermissions(Collections.emptyList());
        } else {
            List<PermissionDto> permissions = source.getPermissions()
                    .stream()
                    .map(this::mapPermission)
                    .toList();
            module.setPermissions(permissions);
        }
        module.setCreateAt(source.getCreateAt());
        module.setUpdateAt(source.getUpdateAt());
        module.setErased(source.getErased());

        return module;
    }

    @Override
    public ModuleResponse update(String id, ModuleRequest request) {
        // Verificar que el módulo a actualizar existe
        UUID moduleId = UUID.fromString(id); // Asumo que ModuleRequest tiene un campo id
        ModulesEntity existingModule = modulesRepository.findById(moduleId)
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));

        // Actualizar los campos del módulo existente
        existingModule.setName(request.getName());
        existingModule.setRoute(request.getRoute());
        existingModule.setIconName(request.getIconName());

        // Manejar la relación padre (similar al save)
        if (request.getParentId() != null) {
            ModulesEntity parent = modulesRepository.findById(UUID.fromString(request.getParentId()))
                    .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                            String.format(Messages.NOT_FOUND, request.getParentId())));
            existingModule.setParent(parent);
        } else {
            existingModule.setParent(null); // Si no viene parentId, se elimina la relación
        }

        // Guardar los cambios y retornar la respuesta
        return mapModule(modulesRepository.save(existingModule));
    }

    @Override
    public ResponseServerDto delete(String id) {
        UUID moduleId = UUID.fromString(id); // Asumo que ModuleRequest tiene un campo id
        ModulesEntity existingModule = modulesRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.NOT_FOUND, id)));
        try {
            existingModule.setErased(true);
            modulesRepository.save(existingModule);
            return new ResponseServerDto(String.format(Messages.ENTITY_DELETE, id), HttpStatus.ACCEPTED, true);
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

    public Specification<ModulesEntity> filterWithParameters(Map<String, String> parameters) {
        return new ModuleSpecification().getSpecificationByFilters(parameters);
    }
}
