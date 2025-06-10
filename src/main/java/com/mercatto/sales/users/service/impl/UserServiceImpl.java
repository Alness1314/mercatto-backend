package com.mercatto.sales.users.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.users.dto.request.UserRequest;
import com.mercatto.sales.users.dto.response.UserResponse;
import com.mercatto.sales.users.entity.UserEntity;
import com.mercatto.sales.users.repository.UserRepository;
import com.mercatto.sales.users.service.UserService;
import com.mercatto.sales.users.specification.UserSpecification;

import jakarta.annotation.PostConstruct;

import com.mercatto.sales.profiles.repository.ProfileRepository;
import com.mercatto.sales.profiles.entity.ProfileEntity;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.files.entity.FileEntity;
import com.mercatto.sales.files.repository.FileRepository;
import com.mercatto.sales.users.dto.CustomUser;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    GenericMapper mapper;

    ModelMapper mapperUpdate = new ModelMapper();

    @PostConstruct
    private void init() {
        mapperUpdate.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public UserResponse save(String companyId, UserRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));
        UserEntity newUser = mapper.map(request, UserEntity.class);
        try {
            if (request.getProfile() == null) {
                throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT, "Profile not found");
            }
            if (request.getImageId() != null) {
                log.info("ingreso a imagen");
                FileEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                        .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                                "image not found"));
                newUser.setImage(imageFile);
            }
            ProfileEntity profile = profileRepository.findById(UUID.fromString(request.getProfile())).orElse(null);
            newUser.setProfile(profile);
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setCompany(company);
            newUser = userRepository.save(newUser);
            return mapperDto(newUser);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Username already exists: " + request.getUsername(), ex);
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Data integrity violation", ex);
        } catch (ResponseStatusException ex) {
            throw ex; // Re-lanzar excepciones ya gestionadas
        } catch (Exception ex) {
            log.error("Error to save user", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", ex);
        }
    }

    @Override
    public UserResponse saveWithoutCompany(UserRequest request) {
        UserEntity newUser = mapper.map(request, UserEntity.class);
        try {
            if (request.getProfile() == null) {
                throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT);
            }
            ProfileEntity profile = profileRepository.findById(UUID.fromString(request.getProfile())).orElse(null);
            newUser.setProfile(profile);
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setCompany(null);
            newUser = userRepository.save(newUser);
            return mapperDto(newUser);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Username already exists: " + request.getUsername(), ex);
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Data integrity violation", ex);
        } catch (ResponseStatusException ex) {
            throw ex; // Re-lanzar excepciones ya gestionadas
        } catch (Exception ex) {
            log.error("Error to save user", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", ex);
        }
    }

    @Override
    public UserResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        UserEntity findUser = userRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "resource not found"));
        return mapperDto(findUser);
    }

    @Override
    public List<UserResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        return userRepository.findAll(filterWithParameters(paramsNew))
                .stream().map(this::mapperDto).toList();
    }

    @Override
    public UserResponse update(String companyId, String id, UserRequest request) {
        // Find existing company and user
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        UserEntity existingUser = userRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in this company"));

        try {
            // Validate profile
            if (request.getProfile() == null) {
                throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT, "Profile not found");
            }

            // Update image if provided
            if (request.getImageId() != null) {
                FileEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                        .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                                "Image not found"));
                existingUser.setImage(imageFile);
            } else {
                existingUser.setImage(null); // Clear image if no imageId provided
            }

            // Update profile
            ProfileEntity profile = profileRepository.findById(UUID.fromString(request.getProfile()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
            existingUser.setProfile(profile);

            // Update other fields
            mapperUpdate.map(request, existingUser);
            existingUser.setCompany(company); // Maintain company relationship

            // Update password if provided
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            UserEntity updatedUser = userRepository.save(existingUser);
            return mapperDto(updatedUser);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Username already exists: " + request.getUsername(), ex);
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Data integrity violation", ex);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating user", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", ex);
        }
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        UserEntity existingUser = userRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in this company"));

        try {
            existingUser.setErased(true);
            userRepository.save(existingUser);
            return new ResponseServerDto(String.format(Messages.DELETE_ENTITY, id), HttpStatus.ACCEPTED, true);
        } catch (Exception e) {
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    String.format(Messages.ERROR_TO_SAVE_ENTITY, e.getMessage()));
        }
    }

    private UserResponse mapperDto(UserEntity source) {
        return mapper.map(source, UserResponse.class);
    }

    public Specification<UserEntity> filterWithParameters(Map<String, String> parameters) {
        return new UserSpecification().getSpecificationByFilters(parameters);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Specification<UserEntity> specification = filterWithParameters(Map.of("username", username));
        UserEntity user = userRepository.findOne(specification).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("User with name: [%s] not found in database", username)));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getProfile().getName()));
        return new CustomUser(user.getUsername(), user.getPassword(), authorities,
                user.getId(), user.getCompany() != null ? user.getCompany().getId() : null);
    }

    @Override
    public UserResponse findByUsername(String username) {
        UserEntity findUser = userRepository.findOne(filterWithParameters(Map.of(Filters.KEY_USERNAME, username)))
                .orElse(null);
        if (findUser == null) {
            return null;
        }
        return mapperDto(findUser);
    }

}
