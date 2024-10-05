package com.systems.fele.users.repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.systems.fele.users.entity.AppUser;
@Repository
public class AppUserRepositoryImpl implements AppUserRepository {

    private JdbcTemplate jdbcTemplate;

    public AppUserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return Optional.ofNullable(
            jdbcTemplate.queryForObject("SELECT * FROM invoices.app_user WHERE email = ? LIMIT 1;", new AppUserRowMapper(), email)
        );
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM invoices.app_user WHERE id = ? LIMIT 1;", new AppUserRowMapper(), id).stream().findFirst();
    }

    @Override
    public List<AppUser> findAll() {
        // TODO:MEDIUM Paginate this API
        return jdbcTemplate.query("SELECT * FROM invoices.app_user;", new AppUserRowMapper());
    }

    @Override
    public void delete(AppUser appUser) {
        jdbcTemplate.update("DELETE FROM invoices.app_user WHERE id = ?", appUser.getId());
    }

    @Override
    public AppUser save(AppUser appUser) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            var stmt = conn.prepareStatement(
            "INSERT INTO invoices.app_user (first_name, last_name, email, password, admin, enabled) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );

            stmt.setString(1, appUser.getFirstName());
            stmt.setString(2, appUser.getLastName());
            stmt.setString(3, appUser.getEmail());
            stmt.setString(4, appUser.getPassword());
            stmt.setBoolean(5, appUser.isAdmin());
            stmt.setBoolean(6, appUser.isAdmin());

            return stmt;
        }, keyHolder);

        var keys = keyHolder.getKeys();
        if (keys == null) {
            throw new RuntimeException("There was an error obaining the last inserted user!");
        } else {
            return new AppUser(
                ((Long) keys.get("id")).longValue(),
                appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getEmail(),
                appUser.getPassword(),
                appUser.isAdmin(),
                appUser.isEnabled()
            );
        }
    }

    @Override
    public AppUser saveAndFlush(AppUser appUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveAndFlush'");
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return jdbcTemplate.queryForList("SELECT id FROM invoices.app_user WHERE email = ? LIMIT 1;", email).isEmpty();
    }
    
}
