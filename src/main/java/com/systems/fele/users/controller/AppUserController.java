package com.systems.fele.users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.systems.fele.users.model.AppUser;
import com.systems.fele.users.repository.AppUserRepository;

@RestController
@RequestMapping("/rest/api/users")
public class AppUserController {

    @Autowired
    AppUserRepository userRepository;

    @PostMapping(path="/create",
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    AppUser create(@RequestParam("name") String name) {
        return userRepository.save(new AppUser(null, name));
    }

    @GetMapping(path="/list",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    List<AppUser> list() {
        return userRepository.findAll();
    }

    @GetMapping(path="/getuser",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    AppUser getUser(@RequestParam("id") long id) {
        var user = userRepository.findById(id);
        return user.orElse(null);
    }

    @DeleteMapping(path="/remove",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    AppUser remove(@RequestParam("id") long id) {
        var user = userRepository.findById(id);

        user.ifPresent(userRepository::delete);
        return user.orElse(null);
    }

}
