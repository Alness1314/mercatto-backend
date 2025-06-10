package com.mercatto.sales.files.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;

import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.files.dto.FileResponse;
import com.mercatto.sales.files.entity.FileEntity;
import com.mercatto.sales.files.repository.FileRepository;
import com.mercatto.sales.files.service.FileService;
import com.mercatto.sales.files.specification.FileSpecification;

import jakarta.annotation.PostConstruct;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileRepository fileRepository;

    private final String baseDir = System.getProperty("user.dir") + File.separator + "assets" + File.separator;
    private final String uploadPath = baseDir + "uploads" + File.separator;

    private String msgNotFound = "File with id or name: [%s] not found";

    ModelMapper mapper = new ModelMapper();

    @PostConstruct
    public void init() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @Override
    public List<FileResponse> find(Map<String, String> params) {
        Specification<FileEntity> specification = filterWithParameters(params);
        return fileRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public FileResponse findOne(String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id);
        FileEntity fileEntity = fileRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(msgNotFound, id)));
        return mapperDto(fileEntity);
    }

    @Override
    public FileResponse storeFile(MultipartFile file) {
        UUID fileId = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        String mimeType = file.getContentType();
        String fileName = fileId + "_" + originalFilename;

        try {
            Path targetLocation = Paths.get(uploadPath).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileEntity fileEntity = FileEntity.builder()
                    .id(fileId)
                    .name(fileName)
                    .mimeType(mimeType)
                    .extension(extension)
                    .createAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .erased(false)
                    .build();

            fileRepository.save(fileEntity);

            fileRepository.save(fileEntity);

            return mapperDto(fileEntity);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store file " + fileName + ". Please try again!", ex);
        }

    }

    @Override
    public ResponseEntity<Resource> downloadFileAsResource(String id) {
        FileResponse fileResponse = this.findOne(id);
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileResponse.getName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format(msgNotFound, fileResponse.getName()));
            }
        } catch (MalformedURLException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format(msgNotFound, fileResponse.getName()), ex);
        }
    }

    public ResponseEntity<Resource> loadFileAsResource(String id) {
        FileResponse fileResponse = this.findOne(id);
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileResponse.getName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format(msgNotFound, fileResponse.getName()));
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format(msgNotFound, fileResponse.getName()), ex);
        }
    }

    private FileResponse mapperDto(FileEntity source) {
        return mapper.map(source, FileResponse.class);
    }

    public Specification<FileEntity> filterWithParameters(Map<String, String> parameters) {
        return new FileSpecification().getSpecificationByFilters(parameters);
    }
}
