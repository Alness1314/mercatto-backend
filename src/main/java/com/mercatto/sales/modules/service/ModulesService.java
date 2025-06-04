package com.mercatto.sales.modules.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.modules.dto.request.ModuleRequest;
import com.mercatto.sales.modules.dto.response.ModuleResponse;
import com.mercatto.sales.modules.entity.ModulesEntity;

public interface ModulesService {
    public ModulesEntity save(ModuleRequest request);
    public ModulesEntity findOne(String id);
    public List<ModulesEntity> findAll();
    public List<ModuleResponse> getFilteredSubmodules(Map<String, String> filters);
}
