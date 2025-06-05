package com.mercatto.sales.files.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.mercatto.sales.files.dto.FileResponse;

public interface FileService {
    public FileResponse storeFile(MultipartFile file);
    public List<FileResponse> find();
    public FileResponse findOne(String id);
    public ResponseEntity<Resource> downloadFileAsResource(String id);
    public ResponseEntity<Resource> loadFileAsResource(String id);
}
