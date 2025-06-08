package com.mercatto.sales.products.controller;

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
import com.mercatto.sales.products.dto.request.ProductRequest;
import com.mercatto.sales.products.dto.response.ProductResponse;
import com.mercatto.sales.products.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Products", description = ".")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/{companyId}/products")
    public ResponseEntity<List<ProductResponse>> findAll(@PathVariable String companyId,
            @RequestParam Map<String, String> parameters) {
        List<ProductResponse> response = productService.find(companyId, parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{companyId}/products/{id}")
    public ResponseEntity<ProductResponse> findOne(@PathVariable String companyId, @PathVariable String id) {
        ProductResponse response = productService.findOne(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/{companyId}/products")
    public ResponseEntity<ProductResponse> save(@PathVariable String companyId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}/products/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable String companyId, @PathVariable String id,
            @RequestBody ProductRequest request) {
        ProductResponse response = productService.update(companyId, id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{companyId}/products/{id}")
    public ResponseEntity<ResponseServerDto> delete(@PathVariable String companyId, @PathVariable String id) {
        ResponseServerDto response = productService.delete(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
