package com.mercatto.sales.files.dto;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class FileResponse{
    private UUID id;
    private String name;
    private String extension;
    private String mimeType;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
