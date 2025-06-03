package com.mercatto.sales.users.dto.response;

import java.util.UUID;

import com.mercatto.sales.common.model.dto.CommonResponse;
import com.mercatto.sales.profiles.dto.response.ProfileResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse extends CommonResponse {
    private String username;
    private String password;
    private ProfileResponse profile;
    private Boolean sendExpirationAlert;
    private UUID companyId;
}
