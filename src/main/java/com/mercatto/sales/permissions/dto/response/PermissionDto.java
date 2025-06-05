package com.mercatto.sales.permissions.dto.response;

import com.mercatto.sales.profiles.dto.response.ProfileDto;

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
public class PermissionDto {
    private ProfileDto profile;
    private Boolean canCreate;
    private Boolean canRead;
    private Boolean canUpdate;
    private Boolean canDelete;
}
