package com.mercatto.sales.transactions.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.transactions.dto.request.SalesRequest;
import com.mercatto.sales.transactions.dto.response.SalesResponse;

public interface SalesService {
    public List<SalesResponse> find(String companyId, Map<String, String> params);
    public SalesResponse findOne(String companyId, String id);
    public SalesResponse save(String companyId, SalesRequest request);
    public SalesResponse update(String companyId, String id, SalesRequest request);
    public ResponseServerDto delete(String companyId, String id);
}
