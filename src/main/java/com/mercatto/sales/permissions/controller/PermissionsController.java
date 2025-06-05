package com.mercatto.sales.permissions.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercatto.sales.modules.dto.request.ModuleRequest;
import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.modules.service.ModulesService;
import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;
import com.mercatto.sales.permissions.entity.PermissionEntity;
import com.mercatto.sales.permissions.service.PermissionService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${api.prefix}/permissions")
@Tag(name = "Permissions", description = ".")
public class PermissionsController {
    @Autowired
    private PermissionService permissionService;

    /*
     * @GetMapping
     * public ResponseEntity<List<PermissionEntity>> findAll(@RequestParam
     * Map<String, String> param) {
     * List<ModulesEntity> response = permissionService.findAll();
     * return new ResponseEntity<>(response, HttpStatus.OK);
     * }
     * 
     * @GetMapping("/{id}")
     * public ResponseEntity<ModulesEntity> findOne(@PathVariable String id) {
     * ModulesEntity response = moduleService.findOne(id);
     * return new ResponseEntity<>(response, HttpStatus.OK);
     * }
     */

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request) {
        PermissionResponse response = permissionService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
