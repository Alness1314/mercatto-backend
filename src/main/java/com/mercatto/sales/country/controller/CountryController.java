package com.mercatto.sales.country.controller;

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

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.country.dto.request.CountryRequest;
import com.mercatto.sales.country.dto.response.CountryResponse;
import com.mercatto.sales.country.service.CountryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/countrys")
@Tag(name = "Countries", description = ".")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @GetMapping()
    public ResponseEntity<List<CountryResponse>> findAll(@RequestParam Map<String, String> parameters) {
        List<CountryResponse> response = countryService.find(parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> findOne(@PathVariable String id) {
        CountryResponse response = countryService.findOne(id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping()
    public ResponseEntity<CountryResponse> save(@Valid @RequestBody CountryRequest request) {
        CountryResponse response = countryService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/multi-save")
    public ResponseEntity<ResponseServerDto> save(@Valid @RequestBody List<CountryRequest> request) {
        ResponseServerDto response = countryService.multiSaving(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
