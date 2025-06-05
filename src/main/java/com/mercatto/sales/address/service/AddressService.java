package com.mercatto.sales.address.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.address.dto.request.AddressRequest;
import com.mercatto.sales.address.dto.response.AddressResponse;
import com.mercatto.sales.common.model.ResponseServerDto;

public interface AddressService {
    public List<AddressResponse> find(Map<String, String> parameters);
    public AddressResponse findOne(String id);
    public AddressResponse save(AddressRequest request);
    public AddressResponse update(String id, AddressRequest request);
    public ResponseServerDto delete(String id);
}
