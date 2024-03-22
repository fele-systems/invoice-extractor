package com.systems.fele.users.service;

import com.systems.fele.users.dto.UserRegisterRequest;
import com.systems.fele.users.dto.UserUpdateRequest;
import com.systems.fele.users.entity.AppUser;

public interface AppUserService {

    /**
     * Registers a new user anonimously (the requester is not authenticated)
     * @param userDto User object
     * @return The newly registered AppUser
     */
    AppUser registerUser(UserRegisterRequest userDto);

    /**
     * Updates a currently existing user.
     * @param id The id of the updated user
     * @param userRegisterRequest User update date
     * @return The AppUser with updated values
     */
    AppUser updateUser(long id, UserUpdateRequest userUpdateRequest);

    /**
     * Returns the currently logged in user.
     * @return The user or null if unauthenticated
     */
    AppUser loggedInUser();

    /**
     * Checks if this email is being used by another user
     * @param email The email
     * @return True if no one is using this email
     */
    boolean isEmailAvailable(String email);

}
