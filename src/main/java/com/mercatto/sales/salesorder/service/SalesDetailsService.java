package com.mercatto.sales.salesorder.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.salesorder.dto.request.SalesDetailsRequest;
import com.mercatto.sales.salesorder.dto.response.SalesDetailsResponse;

public interface SalesDetailsService {
    public List<SalesDetailsResponse> find(String companyId, Map<String, String> params);
    public SalesDetailsResponse findOne(String companyId, String id);
    public SalesDetailsResponse save(String companyId, SalesDetailsRequest request);
    public SalesDetailsResponse update(String companyId, String id, SalesDetailsRequest request);
    public ResponseServerDto delete(String companyId, String id);
}
