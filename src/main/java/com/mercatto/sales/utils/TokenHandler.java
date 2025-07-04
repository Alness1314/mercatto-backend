package com.mercatto.sales.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.mercatto.sales.auth.dto.KeyPrefix;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.exceptions.RestExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

public class TokenHandler {

    private TokenHandler() {
        throw new IllegalStateException("Utility class");
    }

    public static String extract(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(KeyPrefix.PREFIX_TOKEN)) {
            throw new RestExceptionHandler(ApiCodes.API_CODE_401, HttpStatus.UNAUTHORIZED, Messages.TOKEN_ERROR);
        }

        return header.substring(KeyPrefix.PREFIX_TOKEN.length()).trim();
    }
}
