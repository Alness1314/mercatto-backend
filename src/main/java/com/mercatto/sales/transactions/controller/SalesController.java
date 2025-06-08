package com.mercatto.sales.transactions.controller;

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
import com.mercatto.sales.transactions.dto.request.SalesRequest;
import com.mercatto.sales.transactions.dto.response.SalesResponse;
import com.mercatto.sales.transactions.service.SalesService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Sales", description = ".")
public class SalesController {
    @Autowired
    private SalesService salesService;

    @GetMapping("/{companyId}/sales")
    public ResponseEntity<List<SalesResponse>> findAll(@PathVariable String companyId,
            @RequestParam Map<String, String> parameters) {
        List<SalesResponse> response = salesService.find(companyId, parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{companyId}/sales/{id}")
    public ResponseEntity<SalesResponse> findOne(@PathVariable String companyId, @PathVariable String id) {
        SalesResponse response = salesService.findOne(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/{companyId}/sales")
    public ResponseEntity<SalesResponse> save(@PathVariable String companyId,
            @Valid @RequestBody SalesRequest request) {
        SalesResponse response = salesService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}/sales/{id}")
    public ResponseEntity<SalesResponse> update(@PathVariable String companyId, @PathVariable String id,
            @RequestBody SalesRequest request) {
        SalesResponse response = salesService.update(companyId, id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{companyId}/sales/{id}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String companyId, @PathVariable String id) {
        ResponseServerDto response = salesService.delete(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
