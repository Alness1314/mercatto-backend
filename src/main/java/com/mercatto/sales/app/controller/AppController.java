package com.mercatto.sales.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.mercatto.sales.app.service.AppConfigService;
import com.mercatto.sales.common.model.ResponseServerDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AppController {
    @Autowired
    private AppConfigService appConfigService;

    @GetMapping("/")
    public RedirectView redirectToSwagger(HttpServletRequest request) {
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Request Method: {}", request.getMethod());
        log.info("Client IP: {}", request.getRemoteAddr());
        log.info("User-Agent: {}", request.getHeader("User-Agent"));
        return new RedirectView("/swagger-ui/index.html");
    }

    @GetMapping("/api/v1/auth/check-session")
    public ResponseEntity<ResponseServerDto> checkSession(HttpServletRequest request) {
        ResponseServerDto response = appConfigService.checkStatusSession(request);
        return new ResponseEntity<>(response, response.getCode());
    }
}
