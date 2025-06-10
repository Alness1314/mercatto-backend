package com.mercatto.sales.salesorder.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.products.entity.ProductEntity;
import com.mercatto.sales.products.repository.ProductRepository;
import com.mercatto.sales.salesorder.dto.request.SalesDetailsRequest;
import com.mercatto.sales.salesorder.dto.response.SalesDetailsResponse;
import com.mercatto.sales.salesorder.entity.SalesDetailsEntity;
import com.mercatto.sales.salesorder.repository.SalesDetailsRepository;
import com.mercatto.sales.salesorder.service.SalesDetailsService;
import com.mercatto.sales.salesorder.specification.SalesDetailsSpecification;
import com.mercatto.sales.transactions.entity.SalesEntity;
import com.mercatto.sales.transactions.repository.SalesRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SalesDetailServiceImpl implements SalesDetailsService {
    @Autowired
    private SalesDetailsRepository salesDetailsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private GenericMapper mapper;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public List<SalesDetailsResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        Specification<SalesDetailsEntity> specification = filterWithParameters(paramsNew);
        return salesDetailsRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public SalesDetailsResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        SalesDetailsEntity saleDetail = salesDetailsRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Product not found"));
        return mapperDto(saleDetail);
    }

    @Override
    public SalesDetailsResponse save(String companyId, SalesDetailsRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        SalesDetailsEntity saleDetail = mapper.map(request, SalesDetailsEntity.class);
        // ligar la categoria con el producto
        ProductEntity product = productRepository.findById(UUID.fromString(request.getProductId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Product not found"));
        saleDetail.setProduct(product);

        // ligar con la venta
        SalesEntity sales = salesRepository.findById(UUID.fromString(request.getSalesId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Sale not found"));

        saleDetail.setSales(sales);
        saleDetail.setCompany(company.getId());
        try {
            saleDetail = salesDetailsRepository.save(saleDetail);
        } catch (Exception e) {
            log.error("Error to save sale detail {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save sale detail");
        }
        return mapperDto(saleDetail);
    }

    @Override
    public SalesDetailsResponse update(String companyId, String id, SalesDetailsRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    private SalesDetailsResponse mapperDto(SalesDetailsEntity source) {
        return mapper.map(source, SalesDetailsResponse.class);
    }

    public Specification<SalesDetailsEntity> filterWithParameters(Map<String, String> parameters) {
        return new SalesDetailsSpecification().getSpecificationByFilters(parameters);
    }

}
