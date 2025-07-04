package com.mercatto.sales.app.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.app.jwt.JwtDto;
import com.mercatto.sales.app.service.AppConfigService;
import com.mercatto.sales.app.service.DecodeJwtService;
import com.mercatto.sales.app.service.JsonFileReaderService;
import com.mercatto.sales.cities.service.CityService;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.country.dto.request.CountryRequest;
import com.mercatto.sales.country.dto.response.CountryResponse;
import com.mercatto.sales.country.service.CountryService;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.profiles.entity.ProfileEntity;
import com.mercatto.sales.profiles.repository.ProfileRepository;
import com.mercatto.sales.states.dto.response.StateResponse;
import com.mercatto.sales.states.service.StateService;
import com.mercatto.sales.users.dto.request.UserRequest;
import com.mercatto.sales.users.dto.response.UserResponse;
import com.mercatto.sales.users.service.UserService;
import com.mercatto.sales.utils.TokenHandler;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AppConfigServiceImpl implements AppConfigService {
    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService citiesService;

    @Autowired
    private ProfileRepository profileRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private JsonFileReaderService jsonFileReaderService;

    @Autowired
    private DecodeJwtService jwtService;

    private static final String MEXICO = "México";

    @Value("${sys.user.password}")
    private String password;

    @Override
    public ResponseServerDto createDefaultCountries() {
        if (countryService.find(Map.of()).isEmpty()) {
            List<CountryRequest> countries = Arrays.asList(new CountryRequest("Canadá", "CA"),
                    new CountryRequest("Estados Unidos", "US"), new CountryRequest(MEXICO, "MX"));
            return countryService.multiSaving(countries);
        } else {
            return new ResponseServerDto("The countries of North America have already been created", HttpStatus.OK,
                    true,
                    null);
        }

    }

    @Override
    public ResponseServerDto createDefaultStates() {
        Optional<CountryResponse> countryOpt = countryService.find(Map.of("name", MEXICO))
                .stream()
                .findFirst();

        if (countryOpt.isEmpty() || !stateService.find(Collections.emptyMap()).isEmpty()) {
            return new ResponseServerDto("The states of Mexico have already been created",
                    HttpStatus.OK, true);
        }

        List<String> statesMexico = jsonFileReaderService.readJsonFile("json/states_mexico.json");

        return stateService.multiSaving(countryOpt.get().getId().toString(), statesMexico);
    }

    @Override
    public List<ResponseServerDto> createDefaultCities() {
        List<ResponseServerDto> responses = new ArrayList<>();
        responses.add(createCitiesTamaulipas());
        responses.add(createCitiesCDMX());
        return responses;
    }

    private ResponseServerDto createCitiesTamaulipas() {
        Optional<StateResponse> stateOpt = stateService.find(Map.of("name", "Tamaulipas"))
                .stream()
                .findFirst();

        if (stateOpt.isEmpty() || !citiesService.find(Map.of("state", stateOpt.get().getId().toString())).isEmpty()) {
            return new ResponseServerDto("The states of Mexico have already been created",
                    HttpStatus.OK, true, null);
        }
        List<String> municipiosTamaulipas = jsonFileReaderService.readJsonFile("json/tamaulipas.json");

        return citiesService.multiSaving(stateOpt.get().getId().toString(), municipiosTamaulipas);
    }

    private ResponseServerDto createCitiesCDMX() {
        Optional<StateResponse> stateOpt = stateService.find(Map.of("name", "Ciudad de México"))
                .stream()
                .findFirst();

        if (stateOpt.isEmpty() || !citiesService.find(Map.of("state", stateOpt.get().getId().toString())).isEmpty()) {
            return new ResponseServerDto("The cities of cdmx have already been created",
                    HttpStatus.OK, true, null);
        }

        List<String> municipiosCDMX = jsonFileReaderService.readJsonFile("json/cdmx.json");
        return citiesService.multiSaving(stateOpt.get().getId().toString(), municipiosCDMX);
    }

    @Override
    public ResponseServerDto createDefaultProfiles() {
        if (profileRepo.findAll().isEmpty()) {
            ProfileEntity profileDefault = profileRepo.save(ProfileEntity.builder().name("Master").build());
            return new ResponseServerDto("The profiles have already been created", HttpStatus.OK, true,
                    Map.of("data", profileDefault));
        } else {
            return new ResponseServerDto("The profiles have already been created", HttpStatus.OK, true,
                    null);
        }
    }

    @Override
    public ResponseServerDto createDefaultUser() {
        UserResponse stateOpt = userService.findByUsername("master-admin@mercatto.com");
        if (stateOpt != null) {
            return new ResponseServerDto("The user already exist",
                    HttpStatus.OK, true, null);
        } else {
            ProfileEntity profile = profileRepo.findByName("Master").orElse(null);

            if (profile != null) {
                UserRequest user = UserRequest.builder()
                        .username("master-admin@mercatto.com")
                        .password(password)
                        .fullName("Soporte TI")
                        .sendExpirationAlert(false)
                        .profile(profile.getId().toString())
                        .build();

                userService.saveWithoutCompany(user);
            }
            return new ResponseServerDto("The user created",
                    HttpStatus.OK, true, null);
        }
    }

    @Override
    public ResponseServerDto checkStatusSession(HttpServletRequest request) {

        String token = TokenHandler.extract(request);
        if (Boolean.FALSE.equals(jwtService.isValidToken(token))) {
            throw new RestExceptionHandler(ApiCodes.API_CODE_401, HttpStatus.UNAUTHORIZED, Messages.SESSION_ERROR);
        }
        JwtDto jwtDto = jwtService.decodeJwt(token);
        UserResponse user = userService.findByUsername(jwtDto.getBody().getSub());
        if (user == null) {
            return new ResponseServerDto("El usuario no fue encontrado.", HttpStatus.NOT_FOUND, false);
        }
        Map<String, Object> data = Map.of("id", user.getId(), "username", user.getUsername(), "token", token);

        return new ResponseServerDto("Sesión válida.", HttpStatus.ACCEPTED, true, data);
    }

}
