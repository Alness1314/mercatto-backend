package com.mercatto.sales.modules.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.mercatto.sales.modules.dto.request.ModuleRequest;
import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.modules.repository.ModulesRepository;
import com.mercatto.sales.modules.service.ModulesService;
import com.mercatto.sales.modules.specification.ModuleSpecification;

import jakarta.persistence.EntityNotFoundException;

public class ModulesServiceImpl implements ModulesService {
    @Autowired
    private ModulesRepository modulesRepository;

    @Override
    public List<ModulesEntity> getFilteredSubmodules(Map<String, String> filtros) {
        String name = filtros.get("name");
        String profileIdStr = filtros.get("profileId");

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
                        .collect(Collectors.toSet())));

        return hijos;
    }

    @Override
    public ModulesEntity save(ModuleRequest request) {
        ModulesEntity module = new ModulesEntity();
        module.setName(request.getName());
        module.setRoute(request.getRoute());
        module.setIconName(request.getIconName());

        if (request.getParentId() != null) {
            ModulesEntity parent = modulesRepository.findById(UUID.fromString(request.getParentId()))
                    .orElseThrow(() -> new EntityNotFoundException("Parent module not found"));
            module.setParent(parent);
        }

        return modulesRepository.save(module);
    }

    @Override
    public ModulesEntity findOne(String id) {
        return modulesRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + id));
    }

    @Override
    public List<ModulesEntity> findAll() {
        return modulesRepository.findAll()
                .stream()
                .filter(module -> module.getParent() == null) // Solo módulos de nivel superior
                .toList();
    }
}
