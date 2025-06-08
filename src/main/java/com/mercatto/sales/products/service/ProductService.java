package com.mercatto.sales.products.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.products.dto.request.ProductRequest;
import com.mercatto.sales.products.dto.response.ProductResponse;

public interface ProductService {
    public List<ProductResponse> find(String companyId, Map<String, String> params);
    public ProductResponse findOne(String companyId, String id);
    public ProductResponse save(String companyId, ProductRequest request);
    public ProductResponse update(String companyId, String id, ProductRequest request);
    public ResponseServerDto delete(String companyId, String id);
}
