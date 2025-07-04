package com.mercatto.sales.app.service.impl;

import com.mercatto.sales.app.jwt.BodyDto;
import com.mercatto.sales.app.jwt.HeaderDto;
import com.mercatto.sales.app.jwt.JwtDto;
import com.mercatto.sales.app.service.DecodeJwtService;
import com.mercatto.sales.auth.configuration.JwtTokenConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
@RequiredArgsConstructor
public class DecodeJtwServiceImpl implements DecodeJwtService {
    private final JwtTokenConfig jwtTokenConfig;

    @SuppressWarnings("rawtypes")
    @Override
    public JwtDto decodeJwt(String jwtToken) {
        Key key = jwtTokenConfig.getSecretKey();

        // Parseamos el token sin verificar firma (solo lectura)
        Jwt<JwsHeader, Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken);

        // Extraemos header y body
        JwsHeader<?> header = jwt.getHeader();
        Claims claims = jwt.getBody();

        // Mapear manualmente a HeaderDto y BodyDto si no usas reflexión
        HeaderDto headerDto = HeaderDto.builder()
                .alg((String) header.get("alg"))
                .typ((String) header.get("typ"))
                .build();

        BodyDto bodyDto = BodyDto.builder()
                .sub(claims.getSubject())
                .iss(claims.getIssuer())
                .exp(claims.getExpiration() != null ? claims.getExpiration().getTime() / 1000 : null)
                .iat(claims.getIssuedAt() != null ? claims.getIssuedAt().getTime() / 1000 : null)
                .claims(claims) // puedes almacenar todos los claims aquí si BodyDto tiene un Map<String,
                                // Object>
                .build();

        return JwtDto.builder()
                .header(headerDto)
                .body(bodyDto)
                .build();
    }

    @Override
    public Boolean isValidToken(String jwtToken) {
        JwtDto jwtDto = decodeJwt(jwtToken);
        return compareUnixTime(unixTimeNow(), jwtDto.getBody().getExp());
    }

    private Boolean compareUnixTime(Long unixTimeNow, Long unixTimeIn) {
        return unixTimeIn > unixTimeNow;
    }

    private Long unixTimeNow() {
        return System.currentTimeMillis() / 1000;
    }
}
