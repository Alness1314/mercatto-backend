package com.mercatto.sales.products.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.categories.entity.CategoryEntity;
import com.mercatto.sales.categories.repository.CategoryRepository;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.files.entity.FileEntity;
import com.mercatto.sales.files.repository.FileRepository;
import com.mercatto.sales.products.dto.request.ProductRequest;
import com.mercatto.sales.products.dto.response.ProductResponse;
import com.mercatto.sales.products.entity.ProductEntity;
import com.mercatto.sales.products.repository.ProductRepository;
import com.mercatto.sales.products.service.ProductService;
import com.mercatto.sales.products.specification.ProductSpecification;
import com.mercatto.sales.unit.entity.UnitMeasurement;
import com.mercatto.sales.unit.repository.UnitMeasurementRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UnitMeasurementRepo unitMeasurementRepo;

    @Autowired
    private GenericMapper mapper;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public List<ProductResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        Specification<ProductEntity> specification = filterWithParameters(paramsNew);
        return productRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public ProductResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        ProductEntity product = productRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Product not found"));
        return mapperDto(product);
    }

    @Override
    public ProductResponse save(String companyId, ProductRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        ProductEntity product = mapper.map(request, ProductEntity.class);
        // ligar la categoria con el producto
        CategoryEntity categoria = categoryRepository.findById(UUID.fromString(request.getCategoryId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Category not found"));
        product.setCategory(categoria);

        // ligar la unidad de medida con el producto
        UnitMeasurement um = unitMeasurementRepo.findById(UUID.fromString(request.getUnitId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Unit not found"));
        product.setUnit(um);

        if (request.getImageId() != null) {
            log.info("ingreso a imagen");
            FileEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                    .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                            "image not found"));
            product.setImage(imageFile);
        }

        try {
            product.setCompany(company);
            product = productRepository.save(product);
        } catch (Exception e) {
            log.error("Error to save category {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save category");
        }
        return mapperDto(product);
    }

    @Override
    public ProductResponse update(String companyId, String id, ProductRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    private ProductResponse mapperDto(ProductEntity source) {
        return mapper.map(source, ProductResponse.class);
    }

    public Specification<ProductEntity> filterWithParameters(Map<String, String> parameters) {
        return new ProductSpecification().getSpecificationByFilters(parameters);
    }
}
