package com.mercatto.sales.users.service;

import java.util.List;
import java.util.Map;

import com.mercatto.sales.users.dto.request.UserRequest;
import com.mercatto.sales.users.dto.response.UserResponse;
import com.mercatto.sales.common.model.ResponseServerDto;

public interface UserService {
    public UserResponse save(UserRequest request);
    public UserResponse findOne(String id);
    public UserResponse findByUsername(String username);
    public List<UserResponse> find(Map<String, String> params);
    public UserResponse update(String id, UserRequest request);
    public ResponseServerDto delete(String id);
}
