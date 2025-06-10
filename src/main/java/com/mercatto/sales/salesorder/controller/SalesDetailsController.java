package com.mercatto.sales.salesorder.controller;

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
import com.mercatto.sales.salesorder.dto.request.SalesDetailsRequest;
import com.mercatto.sales.salesorder.dto.response.SalesDetailsResponse;
import com.mercatto.sales.salesorder.service.SalesDetailsService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Sales Details", description = ".")
public class SalesDetailsController {
    @Autowired
    private SalesDetailsService salesDetailsService;

    @GetMapping("/{companyId}/sales-details")
    public ResponseEntity<List<SalesDetailsResponse>> findAll(@PathVariable String companyId,
            @RequestParam Map<String, String> parameters) {
        List<SalesDetailsResponse> response = salesDetailsService.find(companyId, parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{companyId}/sales-details/{id}")
    public ResponseEntity<SalesDetailsResponse> findOne(@PathVariable String companyId, @PathVariable String id) {
        SalesDetailsResponse response = salesDetailsService.findOne(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/{companyId}/sales-details")
    public ResponseEntity<SalesDetailsResponse> save(@PathVariable String companyId,
            @Valid @RequestBody SalesDetailsRequest request) {
        SalesDetailsResponse response = salesDetailsService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}/sales-details/{id}")
    public ResponseEntity<SalesDetailsResponse> update(@PathVariable String companyId, @PathVariable String id,
            @RequestBody SalesDetailsRequest request) {
        SalesDetailsResponse response = salesDetailsService.update(companyId, id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{companyId}/sales-details/{id}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String companyId, @PathVariable String id) {
        ResponseServerDto response = salesDetailsService.delete(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
