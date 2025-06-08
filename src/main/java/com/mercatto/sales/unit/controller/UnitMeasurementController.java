package com.mercatto.sales.unit.controller;

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
import com.mercatto.sales.unit.dto.request.UnitMeasurementReq;
import com.mercatto.sales.unit.dto.response.UnitMeasurementResp;
import com.mercatto.sales.unit.service.UnitMeasurementService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Units of measurement", description = ".")
public class UnitMeasurementController {
    @Autowired
    private UnitMeasurementService unitMeasurementService;

    @GetMapping("/{companyId}/unit-measurements")
    public ResponseEntity<List<UnitMeasurementResp>> findAll(@PathVariable String companyId,
            @RequestParam Map<String, String> parameters) {
        List<UnitMeasurementResp> response = unitMeasurementService.find(companyId, parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{companyId}/unit-measurements/{id}")
    public ResponseEntity<UnitMeasurementResp> findOne(@PathVariable String companyId, @PathVariable String id) {
        UnitMeasurementResp response = unitMeasurementService.findOne(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/{companyId}/unit-measurements")
    public ResponseEntity<UnitMeasurementResp> save(@PathVariable String companyId,
            @Valid @RequestBody UnitMeasurementReq request) {
        UnitMeasurementResp response = unitMeasurementService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}/unit-measurements/{id}")
    public ResponseEntity<UnitMeasurementResp> update(@PathVariable String companyId, @PathVariable String id,
            @RequestBody UnitMeasurementReq request) {
        UnitMeasurementResp response = unitMeasurementService.update(companyId, id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{companyId}/unit-measurements/{id}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String companyId, @PathVariable String id) {
        ResponseServerDto response = unitMeasurementService.delete(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
