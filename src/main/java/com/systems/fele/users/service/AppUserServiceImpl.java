package com.systems.fele.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.systems.fele.common.util.StringUtils;
import com.systems.fele.users.dto.UserRegisterRequest;
import com.systems.fele.users.dto.UserUpdateRequest;
import com.systems.fele.users.entity.AppUser;
import com.systems.fele.users.repository.AppUserRepository;
import com.systems.fele.users.security.AppUserPrincipal;

import jakarta.annotation.PostConstruct;

@Service
public class AppUserServiceImpl implements AppUserService {

    
    @Autowired
    public AppUserServiceImpl(PasswordEncoder passwordEncoder, AppUserRepository appUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
    }

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;

    public static final String ADMIN_EMAIL = "admin@fele-systems.com";

    @PostConstruct
    public void init() {
        // create default admin user
        if (appUserRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
            appUserRepository.save(new AppUser(0l, "Admin", "istrator", ADMIN_EMAIL, passwordEncoder.encode("12345678"), true, true));
        }
    }

    @Override
    public AppUser registerUser(UserRegisterRequest userDto) {
        
        if (StringUtils.isNullOrBlank(userDto.getEmail())) {
            throw new RuntimeException("Empty email");
        }

        if (!isEmailAvailable(userDto.getEmail())) {
            throw new RuntimeException("The requested email (" + userDto.getEmail() + ") is already taken. Please use another");
        }

        if (StringUtils.isNullOrBlank(userDto.getPassword())) {
            throw new RuntimeException("Empty password");
        }

        if (StringUtils.isNullOrBlank(userDto.getFirstName())) {
            throw new RuntimeException("Empty firstName");
        }

        if (StringUtils.isNullOrBlank(userDto.getLastName())) {
            throw new RuntimeException("Empty lastName");
        }

        var appUser = new AppUser();

        appUser.setFirstName(userDto.getFirstName());
        appUser.setLastName(userDto.getLastName());
        appUser.setEmail(userDto.getEmail());
        appUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        appUser.setAdmin(userDto.isAdmin());
        appUser.setEnabled(true);
        
        return appUserRepository.save(appUser);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !appUserRepository.findByEmail(email).isPresent();
    }

    

    @Override
    public AppUser updateUser(long id, UserUpdateRequest userUpdateRequest) {
        var actor = loggedInUser();
        if (actor.getId() != id && !actor.isAdmin()) {
            throw new AccessDeniedException("You're not authorized to modify another user.");
        }

        if (!actor.isAdmin() && Boolean.TRUE == userUpdateRequest.getAdmin()) {
            throw new AccessDeniedException("You're not authorized to promote yourself as administrator.");
        }

        // TODO Change this exception to a 404 not found
        var targetUser = appUserRepository.findById(id)
                .orElseThrow(()->new RuntimeException("The user " + id + " does not exist"));
        
        if (userUpdateRequest.getAdmin() == null) userUpdateRequest.setAdmin(targetUser.isAdmin());
        if (userUpdateRequest.getEmail() == null) userUpdateRequest.setEmail(targetUser.getEmail());
        if (userUpdateRequest.getFirstName() == null) userUpdateRequest.setFirstName(targetUser.getFirstName());
        if (userUpdateRequest.getLastName() == null) userUpdateRequest.setLastName(targetUser.getLastName());
        if (userUpdateRequest.getPassword() == null)
            userUpdateRequest.setPassword(targetUser.getPassword());
        else
            userUpdateRequest.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        if (userUpdateRequest.getEnabled() == null) userUpdateRequest.setEnabled(targetUser.isEnabled());

        var appUser = new AppUser();

        appUser.setId(id);
        appUser.setFirstName(userUpdateRequest.getFirstName());
        appUser.setLastName(userUpdateRequest.getLastName());
        appUser.setEmail(userUpdateRequest.getEmail());
        appUser.setPassword(userUpdateRequest.getPassword());
        appUser.setAdmin(userUpdateRequest.getAdmin());
        appUser.setEnabled(userUpdateRequest.getEnabled());
        
        return appUserRepository.saveAndFlush(appUser);
    }
    
    @Override
    public AppUser loggedInUser() {
        return ((AppUserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()).getAppUser();
    }

    @Override
    public AppUser getUserById(long appUserId) {
        return appUserRepository.findById(appUserId).get();
    }

    @Override
    public AppUser getUserByEmail(String email) {
        return appUserRepository.findByEmail(email).get();
    }

    
}
