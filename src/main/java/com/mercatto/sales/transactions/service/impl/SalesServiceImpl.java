package com.mercatto.sales.transactions.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.transactions.dto.request.SalesRequest;
import com.mercatto.sales.transactions.dto.response.SalesResponse;
import com.mercatto.sales.transactions.service.SalesService;
import com.mercatto.sales.users.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SalesServiceImpl implements SalesService{
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<SalesResponse> find(String companyId, Map<String, String> params) {
        throw new UnsupportedOperationException("Unimplemented method 'find'");
    }

    @Override
    public SalesResponse findOne(String companyId, String id) {
        throw new UnsupportedOperationException("Unimplemented method 'findOne'");
    }

    @Override
    public SalesResponse save(String companyId, SalesRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public SalesResponse update(String companyId, String id, SalesRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
    
}
