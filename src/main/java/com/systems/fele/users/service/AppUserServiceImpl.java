package com.systems.fele.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.systems.fele.common.util.StringUtils;
import com.systems.fele.users.dto.UserRegisterRequest;
import com.systems.fele.users.entity.AppUser;
import com.systems.fele.users.repository.AppUserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AppUserServiceImpl implements AppUserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository appUserRepository;

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
            throw new RuntimeException("Invalid empty username");
        }

        if (!isEmailAvailable(userDto.getEmail())) {
            throw new RuntimeException("The requested email (" + userDto.getEmail() + ") is already taken. Please use another");
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

    
}
