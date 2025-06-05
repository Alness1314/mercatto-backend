package com.mercatto.sales.modules.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.common.model.ResponseServerDto;
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

@Service
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
                    .orElseThrow(() -> new EntityNotFoundException("Parent module not found"));
            module.setParent(parent);
        }

        return mapModule(modulesRepository.save(module));
    }

    @Override
    public ModuleResponse findOne(String id) {
        ModulesEntity module = modulesRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + id));

        return mapModule(module);
    }

    @Override
    public List<ModuleResponse> findAll() {
        return modulesRepository.findAll()
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
                response.add(Map.of("module", resp.getName(), "status", true));
            } else {
                response.add(Map.of("module", item.getName(), "status", false));
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
}
