package com.systems.fele.users.repository;

import java.util.List;
import java.util.Optional;

import com.systems.fele.users.entity.AppUser;

public interface AppUserRepository /*extends Repository<AppUser, Long>*/ {
    
    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findById(Long id);

    List<AppUser> findAll();

    void delete(AppUser appUser);

    AppUser save(AppUser appUser);

    AppUser saveAndFlush(AppUser appUser);

    boolean isEmailAvailable(String email);
}