package com.mercatto.sales.profiles.controller;

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
import com.mercatto.sales.modules.dto.response.ModuleResponse;
import com.mercatto.sales.modules.service.ModulesService;
import com.mercatto.sales.profiles.dto.request.ProfileRequest;
import com.mercatto.sales.profiles.dto.response.ProfileResponse;
import com.mercatto.sales.profiles.service.ProfileService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Profiles", description = ".profiles")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @Autowired
    private ModulesService moduleService;

    @GetMapping("/{companyId}/profiles")
    public ResponseEntity<List<ProfileResponse>> findAll(@PathVariable String companyId,
            @RequestParam Map<String, String> param) {
        List<ProfileResponse> response = profileService.find(companyId, param);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{companyId}/profiles/{id}")
    public ResponseEntity<ProfileResponse> findOne(@PathVariable String companyId, @PathVariable String id) {
        ProfileResponse response = profileService.findOne(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{companyId}/profiles")
    public ResponseEntity<ProfileResponse> create(@PathVariable String companyId, @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profiles/{profileId}/modules")
    public ResponseEntity<List<ModuleResponse>> find(@PathVariable String profileId,
            @RequestParam("name") String param) {
        List<ModuleResponse> response = moduleService.getFilteredSubmodules(profileId, param);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{companyId}/profiles/multi-save")
    public ResponseEntity<ResponseServerDto> saveAll(@PathVariable String companyId,
            @RequestBody List<ProfileRequest> request) {
        ResponseServerDto response = profileService.multiSaving(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{companyId}/profiles/{id}")
    public ResponseEntity<ProfileResponse> update(@PathVariable String companyId, @PathVariable String id,
            @Valid @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.update(companyId, id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{companyId}/profiles/{id}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String companyId, @PathVariable String id) {
        ResponseServerDto response = profileService.delete(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
