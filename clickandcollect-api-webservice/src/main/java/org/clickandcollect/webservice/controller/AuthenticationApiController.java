package org.clickandcollect.webservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.clickandcollect.business.contract.AuthenticationService;
import org.clickandcollect.model.entity.Restaurant;
import org.clickandcollect.webservice.dto.AuthToken;
import org.clickandcollect.webservice.dto.LoginFormDto;
import org.clickandcollect.webservice.dto.RegistrationFormDto;
import org.clickandcollect.webservice.dto.RestaurantDto;
import org.clickandcollect.webservice.mapper.RestaurantMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;
    private final RestaurantMapper restaurantMapper;

    public AuthenticationApiController(AuthenticationService authenticationService, RestaurantMapper restaurantMapper) {
        this.authenticationService = authenticationService;
        this.restaurantMapper = restaurantMapper;
    }

    @PostMapping("register")
    public ResponseEntity<RestaurantDto> register(@Valid @RequestBody RegistrationFormDto registerForm) {
        log.info("Requesting register new restaurant {}", registerForm.getEmail());
        Restaurant restaurant = this.authenticationService.register(this.restaurantMapper.registerFormToRestaurant(registerForm));
        log.info("Restaurant '{}' created", restaurant.getId());
        return new ResponseEntity<>(this.restaurantMapper.restaurantToRestaurantDto(restaurant), HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<AuthToken> login(@Valid @RequestBody LoginFormDto loginFormDto) {
        log.info("User login attempt '{}'", loginFormDto.getEmail());


        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("register")
    public ResponseEntity<Boolean> emailExistsBoolean(@RequestParam(value = "email") String email) {
        log.info("Checking if email '{}' is present in database", email);
        boolean response = this.authenticationService.checkEmailExistsBoolean(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.HEAD, value="register")
    public ResponseEntity<Void> emailExists(@RequestParam(value = "email") String email) {
        log.info("Checking if email '{}' is present in database", email);
        this.authenticationService.checkEmailExists(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
