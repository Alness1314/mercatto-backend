package com.mercatto.sales.users.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.files.dto.FileResponse;
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
public class UserResponse {
    private UUID id;
    private String username;
    private String password;
    private String fullName;
    private FileResponse image;
    private ProfileResponse profile;
    private Boolean sendExpirationAlert;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
