package com.mercatto.sales.modules.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.modules.dto.request.ModuleRequest;
import com.mercatto.sales.modules.dto.response.ModuleResponse;

public interface ModulesService {
    public ModuleResponse save(ModuleRequest request);
    public ModuleResponse findOne(String id);
    public ModuleResponse update(String id, ModuleRequest request);
    public ResponseServerDto delete(String id);
    public List<ModuleResponse> findAll(Map<String, String> params);
    public List<ModuleResponse> getFilteredSubmodules(String profileId, String name);
    public ResponseServerDto multiSave(List<ModuleRequest> modules);
}
