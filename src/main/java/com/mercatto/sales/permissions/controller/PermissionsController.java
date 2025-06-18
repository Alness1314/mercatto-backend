package com.mercatto.sales.permissions.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.request.PermissionUpdateReq;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;
import com.mercatto.sales.permissions.service.PermissionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Permissions", description = ".")
public class PermissionsController {
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/{companyId}/permissions")
    public ResponseEntity<PermissionResponse> create(@PathVariable String companyId,
            @RequestBody PermissionRequest request) {
        PermissionResponse response = permissionService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{companyId}/permissions")
    public ResponseEntity<List<PermissionResponse>> getAll(@PathVariable String companyId,
            @RequestParam Map<String, String> params) {
        List<PermissionResponse> response = permissionService.find(companyId, params);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{companyId}/permissions/profile/{profileId}/module/{moduleId}")
    public ResponseEntity<PermissionResponse> getOne(@PathVariable String companyId,
            @PathVariable String profileId, @PathVariable String moduleId) {
        PermissionResponse response = permissionService.findOne(companyId, profileId, moduleId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}/permissions/profile/{profileId}/module/{moduleId}")
    public ResponseEntity<PermissionResponse> update(@PathVariable String companyId,
            @PathVariable String profileId, @PathVariable String moduleId,
            @RequestBody PermissionUpdateReq request) {
        PermissionResponse response = permissionService.update(companyId, profileId, moduleId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{companyId}/permissions/profile/{profileId}/module/{moduleId}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String companyId,
            @PathVariable String profileId, @PathVariable String moduleId) {
        ResponseServerDto response = permissionService.delete(companyId, profileId, moduleId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
