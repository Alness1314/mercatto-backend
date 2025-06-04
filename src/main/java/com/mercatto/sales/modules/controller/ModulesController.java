package com.mercatto.sales.modules.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.modules.dto.request.ModuleRequest;
import com.mercatto.sales.modules.dto.response.ModuleResponse;
import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.modules.service.ModulesService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${api.prefix}/modules")
@Tag(name = "Modules", description = ".")
public class ModulesController {
     @Autowired
    private ModulesService moduleService;

    @GetMapping
    public ResponseEntity<List<ModulesEntity>> findAll(@RequestParam Map<String, String> param) {
        List<ModulesEntity> response = moduleService.findAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModulesEntity> findOne(@PathVariable String id) {
        ModulesEntity response = moduleService.findOne(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ModulesEntity> create(@RequestBody ModuleRequest request) {
        ModulesEntity response = moduleService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*@PostMapping("/all")
    public ResponseEntity<ResponseServerDto> createAll(@RequestBody List<ModuleRequest> request) {
        ResponseServerDto response = moduleService.multiSave(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping()
    public ResponseEntity<ModuleResponse> asignmodule(@RequestParam String parentModuleId,
            @RequestParam String childModuleId) {
        ModuleResponse response = moduleService.assignChildToParent(parentModuleId, childModuleId);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }*/

    @GetMapping("/all")
    public ResponseEntity<List<ModuleResponse>> find(@RequestParam Map<String, String> param) {
        List<ModuleResponse> response = moduleService.getFilteredSubmodules(param);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
