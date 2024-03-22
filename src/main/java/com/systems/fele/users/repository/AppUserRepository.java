package com.systems.fele.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systems.fele.users.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    
    Optional<AppUser> findByEmail(String email);

}