package com.mercatto.sales.permissions.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercatto.sales.permissions.dto.request.PermissionRequest;
import com.mercatto.sales.permissions.dto.response.PermissionResponse;
import com.mercatto.sales.permissions.service.PermissionService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${api.prefix}/permissions")
@Tag(name = "Permissions", description = ".")
public class PermissionsController {
    @Autowired
    private PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request) {
        PermissionResponse response = permissionService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
