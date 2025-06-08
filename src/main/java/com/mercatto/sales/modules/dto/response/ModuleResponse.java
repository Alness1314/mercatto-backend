package com.mercatto.sales.modules.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.mercatto.sales.permissions.dto.response.PermissionDto;

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
public class ModuleResponse{
    private UUID id;
    private String name;
    private String route;
    private String iconName;
    List<PermissionDto> permissions;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
