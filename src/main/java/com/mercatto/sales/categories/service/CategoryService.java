package com.mercatto.sales.categories.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.categories.dto.request.CategoryRequest;
import com.mercatto.sales.categories.dto.response.CategoryResponse;
import com.mercatto.sales.common.model.ResponseServerDto;

public interface CategoryService {
     public List<CategoryResponse> find(String companyId, Map<String, String> params);
    public CategoryResponse findOne(String companyId, String id);
    public CategoryResponse save(String companyId, CategoryRequest request);
    public CategoryResponse update(String companyId, String id, CategoryRequest request);
    public ResponseServerDto delete(String companyId, String id);
}
