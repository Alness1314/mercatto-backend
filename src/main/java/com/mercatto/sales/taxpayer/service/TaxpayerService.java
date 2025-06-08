package com.mercatto.sales.taxpayer.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.taxpayer.dto.request.TaxpayerRequest;
import com.mercatto.sales.taxpayer.dto.response.TaxpayerResponse;

public interface TaxpayerService {
    public List<TaxpayerResponse> find(Map<String, String> parameters);
    public TaxpayerResponse findOne(String id);
    public TaxpayerResponse save(TaxpayerRequest request);
    public TaxpayerResponse update(String id, TaxpayerRequest request);
    public ResponseServerDto delete(String id);
}
