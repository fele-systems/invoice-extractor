package com.systems.fele.users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.systems.fele.common.http.ApiError;
import com.systems.fele.users.dto.UserDto;
import com.systems.fele.users.dto.UserRegisterRequest;
import com.systems.fele.users.dto.UserUpdateRequest;
import com.systems.fele.users.repository.AppUserRepository;
import com.systems.fele.users.service.AppUserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/rest/api/users")
public class AppUserController {

    
    @Autowired
    public AppUserController(AppUserService appUserService, AppUserRepository userRepository) {
        this.appUserService = appUserService;
        this.userRepository = userRepository;
    }

    final AppUserService appUserService;
    final AppUserRepository userRepository;

    @PostMapping(path = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    UserDto register(@RequestBody UserRegisterRequest userDto) {
        var appUser = appUserService.registerUser(userDto);

        return UserDto.fromAppUser(appUser);
    }

    @GetMapping(path = {"", "/" }, produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<List<UserDto>> list() {
        var users = userRepository.findAll().stream().map(UserDto::fromAppUser).toList();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    UserDto getUser(@NonNull @PathVariable("id") Long id) {
        var user = userRepository.findById(id);
        return user.map(UserDto::fromAppUser).orElseThrow();
    }

    @PutMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    UserDto updateUser(@NonNull @PathVariable("id") Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return UserDto.fromAppUser(appUserService.updateUser(id, userUpdateRequest));
    }

    @DeleteMapping(path = "/remove/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    UserDto remove(@NonNull @PathVariable("id") Long id) {
        var user = userRepository.findById(id);

        user.ifPresent(userRepository::delete);
        return user.map(UserDto::fromAppUser).orElse(null);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        log.error("Something went wrong", ex);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
