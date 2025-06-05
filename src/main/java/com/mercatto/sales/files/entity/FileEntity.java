package com.mercatto.sales.files.entity;

import com.mercatto.sales.common.model.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FileEntity extends CommonEntity {
    @Column(name = "name", nullable = false, columnDefinition = "character varying(256)")
    private String name;

    @Column(name = "extension", nullable = false, columnDefinition = "character varying(64)")
    private String extension;

    @Column(name = "mime_type", nullable = false, columnDefinition = "character varying(128)")
    private String mimeType;
}
