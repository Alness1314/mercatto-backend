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

import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;
import com.mercatto.sales.permissions.service.PermissionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<List<PermissionResponse>> getMethodName(@PathVariable String companyId,
            @RequestParam Map<String, String> params) {
        List<PermissionResponse> response = permissionService.find(companyId, params);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
