package com.mercatto.sales.categories.controller;

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

import com.mercatto.sales.categories.dto.request.CategoryRequest;
import com.mercatto.sales.categories.dto.response.CategoryResponse;
import com.mercatto.sales.categories.service.CategoryService;
import com.mercatto.sales.common.model.ResponseServerDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Categories", description = ".")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{companyId}/categories")
    public ResponseEntity<List<CategoryResponse>> findAll(@PathVariable String companyId,
            @RequestParam Map<String, String> parameters) {
        List<CategoryResponse> response = categoryService.find(companyId, parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{companyId}/categories/{id}")
    public ResponseEntity<CategoryResponse> findOne(@PathVariable String companyId, @PathVariable String id) {
        CategoryResponse response = categoryService.findOne(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/{companyId}/categories")
    public ResponseEntity<CategoryResponse> save(@PathVariable String companyId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}/categories/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable String companyId, @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.update(companyId, id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{companyId}/categories/{id}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String companyId, @PathVariable String id) {
        ResponseServerDto response = categoryService.delete(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
