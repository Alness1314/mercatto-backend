package com.mercatto.sales.users.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercatto.sales.users.dto.request.UserRequest;
import com.mercatto.sales.users.dto.response.UserResponse;
import com.mercatto.sales.users.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${api.prefix}/company")
@Tag(name = "Users", description = ".")
public class UserController {
     @Autowired
    private UserService userService;

    @GetMapping("/{companyId}/users")
    public ResponseEntity<List<UserResponse>> findAll(@PathVariable String companyId, @RequestParam Map<String, String> param) {
        List<UserResponse> response = userService.find(companyId, param);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{companyId}/users/{id}")
    public ResponseEntity<UserResponse> findOne(@PathVariable String companyId, @PathVariable String id) {
        UserResponse response = userService.findOne(companyId, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{companyId}/users")
    public ResponseEntity<UserResponse> postMethodName(@PathVariable String companyId, @RequestBody UserRequest request) {
        UserResponse response = userService.save(companyId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
