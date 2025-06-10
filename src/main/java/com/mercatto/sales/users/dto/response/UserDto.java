package com.mercatto.sales.users.dto.response;

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
public class UserDto {
    private UUID id;
    private String username;
    private String fullName;
    private FileResponse image;
    private ProfileResponse profile;
    private Boolean erased;
}
