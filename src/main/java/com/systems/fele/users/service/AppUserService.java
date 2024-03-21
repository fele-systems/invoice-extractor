package com.systems.fele.users.service;

import com.systems.fele.users.dto.UserRegisterRequest;
import com.systems.fele.users.entity.AppUser;

public interface AppUserService {

    /**
     * Registers a new user anonimously (the requester is not authenticated)
     * @param userDto User object
     * @return The newly registeres AppUser
     */
    AppUser registerUser(UserRegisterRequest userDto);


    /**
     * Checks if this email is being used by another user
     * @param email The email
     * @return True if no one is using this email
     */
    boolean isEmailAvailable(String email);

}
