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
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/users")
@Tag(name = "Users", description = ".")
public class UserAdminController {
    @Autowired
    private UserService userService;

    @PostMapping("/admin")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.saveWithoutCompany(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<UserResponse> findOne(@PathVariable String id) {
        UserResponse response = userService.findOneWithoutCompany(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<UserResponse>> find(@RequestParam Map<String, String> params) {
        List<UserResponse> response = userService.findWithoutCompany(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
