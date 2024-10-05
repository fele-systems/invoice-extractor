package com.systems.fele.users.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.systems.fele.users.entity.AppUser;

public class AppUserRowMapper implements RowMapper<AppUser> {

    @Override
    @Nullable
    public AppUser mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        return new AppUser(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getBoolean("admin"),
            rs.getBoolean("enabled")
        );
    }
    
}
