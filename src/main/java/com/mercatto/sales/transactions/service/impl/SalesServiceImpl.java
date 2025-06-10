package com.mercatto.sales.transactions.service.impl;

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
import com.mercatto.sales.transactions.dto.request.SalesRequest;
import com.mercatto.sales.transactions.dto.response.SalesResponse;
import com.mercatto.sales.transactions.entity.SalesEntity;
import com.mercatto.sales.transactions.repository.SalesRepository;
import com.mercatto.sales.transactions.service.SalesService;
import com.mercatto.sales.transactions.specification.SalesSpecification;
import com.mercatto.sales.users.entity.UserEntity;
import com.mercatto.sales.users.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SalesServiceImpl implements SalesService{
    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<SalesResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        Specification<SalesEntity> specification = filterWithParameters(paramsNew);
        return salesRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public SalesResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        SalesEntity sales = salesRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Sale not found"));
        return mapperDto(sales);
    }

    @Override
    public SalesResponse save(String companyId, SalesRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        SalesEntity sale = mapper.map(request, SalesEntity.class);
        // ligar el usuario con la venta
        UserEntity user = userRepository.findById(UUID.fromString(request.getUserId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "User not found"));
        sale.setUser(user);
        try {
            sale.setCompany(company);
            sale = salesRepository.save(sale);
        } catch (Exception e) {
            log.error("Error to save sale {}", e.getMessage());
            e.printStackTrace();
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save sale");
        }
        return mapperDto(sale);
    }

    @Override
    public SalesResponse update(String companyId, String id, SalesRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
    
    private SalesResponse mapperDto(SalesEntity source) {
        return mapper.map(source, SalesResponse.class);
    }

    public Specification<SalesEntity> filterWithParameters(Map<String, String> parameters) {
        return new SalesSpecification().getSpecificationByFilters(parameters);
    }
}
