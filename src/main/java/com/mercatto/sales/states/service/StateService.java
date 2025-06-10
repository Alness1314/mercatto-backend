package com.mercatto.sales.states.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.states.dto.request.StateRequest;
import com.mercatto.sales.states.dto.response.StateResponse;

public interface StateService {
    public List<StateResponse> find(Map<String, String> parameters);
    public StateResponse findOne(String id);
    public StateResponse save(StateRequest request);
    public ResponseServerDto multiSaving(String countryId, List<String> stateList);
}
