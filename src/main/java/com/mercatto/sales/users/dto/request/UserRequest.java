package com.mercatto.sales.users.dto.request;

import com.mercatto.sales.annotations.build.IsUUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class UserRequest {
    @NotNull
    @Email
    private String username;

    @NotNull
    private String password;
    
    @NotNull
    private String fullName;
    
    @NotNull
    private Boolean sendExpirationAlert;
    
    @IsUUID
    private String imageId;
    
    @IsUUID
    @NotNull
    private String profile;
}
