package com.mercatto.sales.cities.controller;

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

import com.mercatto.sales.cities.dto.request.CityRequest;
import com.mercatto.sales.cities.dto.response.CityResponse;
import com.mercatto.sales.cities.service.CityService;
import com.mercatto.sales.common.model.ResponseServerDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/cities")
@Tag(name = "Cities", description = ".")
public class CityController {
    @Autowired
    private CityService cityService;

    @GetMapping()
    public ResponseEntity<List<CityResponse>> findAll(@RequestParam Map<String, String> parameters) {
        List<CityResponse> response = cityService.find(parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> findOne(@PathVariable String id) {
        CityResponse response = cityService.findOne(id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping
    public ResponseEntity<CityResponse> save(@Valid @RequestBody CityRequest request) {
        CityResponse response = cityService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/all/{stateId}")
    public ResponseEntity<ResponseServerDto> multiSave(@PathVariable String stateId,
            @Valid @RequestBody List<String> request) {
        ResponseServerDto response = cityService.multiSaving(stateId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
