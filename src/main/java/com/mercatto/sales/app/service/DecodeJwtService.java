package com.mercatto.sales.app.service;

import com.mercatto.sales.app.dto.JwtDto;

public interface DecodeJwtService {
    public JwtDto decodeJwt(String jwtToken);
    public Boolean isValidToken(String jwtToken);
}
