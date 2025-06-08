package com.mercatto.sales.company.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.dto.request.CompanyRequest;
import com.mercatto.sales.company.dto.response.CompanyResponse;

public interface CompanyService {
    public List<CompanyResponse> find(Map<String, String> parameters);
    public CompanyResponse findOne(String id);
    public CompanyResponse save(CompanyRequest request);
    public CompanyResponse update(String id, CompanyRequest request);
    public ResponseServerDto delete(String id);
}
