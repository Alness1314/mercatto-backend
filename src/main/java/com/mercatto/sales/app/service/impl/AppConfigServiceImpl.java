package com.mercatto.sales.app.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.app.dto.JwtDto;
import com.mercatto.sales.app.service.AppConfigService;
import com.mercatto.sales.app.service.DecodeJwtService;
import com.mercatto.sales.app.service.JsonFileReaderService;
import com.mercatto.sales.auth.dto.KeyPrefix;
import com.mercatto.sales.cities.service.CityService;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.country.dto.request.CountryRequest;
import com.mercatto.sales.country.dto.response.CountryResponse;
import com.mercatto.sales.country.service.CountryService;
import com.mercatto.sales.profiles.dto.request.ProfileRequest;
import com.mercatto.sales.profiles.dto.response.ProfileResponse;
import com.mercatto.sales.profiles.service.ProfileService;
import com.mercatto.sales.states.dto.response.StateResponse;
import com.mercatto.sales.states.service.StateService;
import com.mercatto.sales.users.dto.request.UserRequest;
import com.mercatto.sales.users.dto.response.UserResponse;
import com.mercatto.sales.users.service.UserService;

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
    private ProfileService profileService;

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
        if (profileService.find(Map.of()).isEmpty()) {
            List<ProfileRequest> profiles = Arrays.asList(new ProfileRequest("Master"),
                    new ProfileRequest("Administrador"), new ProfileRequest("Empleado"));
            return profileService.multiSaving(profiles);
        } else {
            return new ResponseServerDto("The profiles have already been created", HttpStatus.OK, true,
                    null);
        }
    }

    @Override
    public ResponseServerDto createDefaultUser() {
        UserResponse stateOpt = userService.findByUsername("master-admin@msn.com");
        if (stateOpt != null) {
            return new ResponseServerDto("The user already exist",
                    HttpStatus.OK, true, null);
        }

        Optional<ProfileResponse> profile = profileService.find(Map.of("name", "Master"))
                .stream()
                .findFirst();

        if (!profile.isEmpty()) {

            UserRequest user = UserRequest.builder()
                    .username("master-admin@msn.com")
                    .password(password)
                    .fullName("Soporte TI")
                    .sendExpirationAlert(false)
                    .profile(profile.get().getId().toString())
                    .build();

            userService.saveWithoutCompany(user);
        }
        return new ResponseServerDto("The user created",
                HttpStatus.OK, true, null);

    }

    @Override
    public ResponseServerDto checkStatusSession(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(KeyPrefix.PREFIX_TOKEN)) {
            return new ResponseServerDto("Sesión no válida o expirada.", HttpStatus.UNAUTHORIZED, false);
        }

        String token = header.substring(KeyPrefix.PREFIX_TOKEN.length()).trim();

        if (Boolean.FALSE.equals(jwtService.isValidToken(token))) {
            return new ResponseServerDto("Sesión no válida o expirada.", HttpStatus.UNAUTHORIZED, false);
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
