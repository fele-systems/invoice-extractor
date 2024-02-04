package com.systems.fele.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systems.fele.users.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    
}