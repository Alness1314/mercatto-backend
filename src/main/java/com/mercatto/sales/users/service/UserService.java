package com.mercatto.sales.users.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.users.dto.request.UserRequest;
import com.mercatto.sales.users.dto.response.UserResponse;
import com.mercatto.sales.common.model.ResponseServerDto;

public interface UserService {
    public UserResponse save(String companyId, UserRequest request);
    public UserResponse saveWithoutCompany(UserRequest request);
    public UserResponse findOne(String companyId, String id);
    public UserResponse findOneWithoutCompany(String id);
    public UserResponse findByUsername(String username);
    public List<UserResponse> find(String companyId, Map<String, String> params);
    public List<UserResponse> findWithoutCompany(Map<String, String> params);
    public UserResponse update(String companyId, String id, UserRequest request);
    public ResponseServerDto delete(String companyId, String id);
}
