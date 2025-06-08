package com.mercatto.sales.unit.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.unit.dto.request.UnitMeasurementReq;
import com.mercatto.sales.unit.dto.response.UnitMeasurementResp;

public interface UnitMeasurementService {
    public List<UnitMeasurementResp> find(String companyId, Map<String, String> params);
    public UnitMeasurementResp findOne(String companyId, String id);
    public UnitMeasurementResp save(String companyId, UnitMeasurementReq request);
    public UnitMeasurementResp update(String companyId, String id, UnitMeasurementReq request);
    public ResponseServerDto delete(String companyId, String id);
}
