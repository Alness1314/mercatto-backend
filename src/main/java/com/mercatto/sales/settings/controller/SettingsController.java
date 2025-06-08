package com.mercatto.sales.settings.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.settings.dto.request.SettingsRequest;
import com.mercatto.sales.settings.dto.response.SettingsResponse;
import com.mercatto.sales.settings.service.SettingsService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Settings", description = ".")
public class SettingsController {
    @Autowired
    private SettingsService settingsService;

    @GetMapping("/{companyId}/settings")
    public ResponseEntity<List<SettingsResponse>> findAll(@PathVariable String companyId,
            @RequestParam Map<String, String> parameters) {
        List<SettingsResponse> response = settingsService.find(companyId, parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{companyId}/settings/{id}")
    public ResponseEntity<SettingsResponse> findOne(@PathVariable String companyId, @PathVariable String id) {
        SettingsResponse response = settingsService.findOne(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/{companyId}/settings")
    public ResponseEntity<SettingsResponse> save(@PathVariable String companyId,
            @Valid @RequestBody SettingsRequest request) {
        SettingsResponse response = settingsService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}/settings/{id}")
    public ResponseEntity<SettingsResponse> update(@PathVariable String companyId, @PathVariable String id,
            @RequestBody SettingsRequest request) {
        SettingsResponse response = settingsService.update(companyId, id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{companyId}/settings/{id}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String companyId, @PathVariable String id) {
        ResponseServerDto response = settingsService.delete(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
