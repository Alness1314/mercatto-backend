package com.mercatto.sales.taxpayer.controller;

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
import com.mercatto.sales.taxpayer.dto.request.TaxpayerRequest;
import com.mercatto.sales.taxpayer.dto.response.TaxpayerResponse;
import com.mercatto.sales.taxpayer.service.TaxpayerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/taxpayer")
@Tag(name = "Taxpayer", description = ".")
public class TaxpayerController {
    @Autowired
    private TaxpayerService taxpayerService;

    @GetMapping
    public ResponseEntity<List<TaxpayerResponse>> findAll(@RequestParam Map<String, String> parameters) {
        List<TaxpayerResponse> response = taxpayerService.find(parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxpayerResponse> findOne(@PathVariable String id) {
        TaxpayerResponse response = taxpayerService.findOne(id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping
    public ResponseEntity<TaxpayerResponse> save(@Valid @RequestBody TaxpayerRequest request) {
        TaxpayerResponse response = taxpayerService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxpayerResponse> update(@PathVariable String id, @RequestBody TaxpayerRequest request) {
        TaxpayerResponse response = taxpayerService.update(id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String id) {
        ResponseServerDto response = taxpayerService.delete(id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
