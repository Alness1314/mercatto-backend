package com.mercatto.sales.categories.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.categories.dto.request.CategoryRequest;
import com.mercatto.sales.categories.dto.response.CategoryResponse;
import com.mercatto.sales.categories.entity.CategoryEntity;
import com.mercatto.sales.categories.repository.CategoryRepository;
import com.mercatto.sales.categories.service.CategoryService;
import com.mercatto.sales.categories.specification.CategorySpecification;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<CategoryResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        Specification<CategoryEntity> specification = filterWithParameters(paramsNew);
        return categoryRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public CategoryResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        CategoryEntity category = categoryRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Category not found"));
        return mapperDto(category);
    }

    @Override
    public CategoryResponse save(String companyId, CategoryRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        CategoryEntity category = mapper.map(request, CategoryEntity.class);
        try {
            category.setCompany(company);
            category = categoryRepository.save(category);
        } catch (Exception e) {
            log.error("Error to save category {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save category");
        }
        return mapperDto(category);
    }

    @Override
    public CategoryResponse update(String companyId, String id, CategoryRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    private CategoryResponse mapperDto(CategoryEntity source) {
        return mapper.map(source, CategoryResponse.class);
    }

    public Specification<CategoryEntity> filterWithParameters(Map<String, String> parameters) {
        return new CategorySpecification().getSpecificationByFilters(parameters);
    }

}
