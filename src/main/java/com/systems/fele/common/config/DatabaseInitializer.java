package com.systems.fele.common.config;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.java.Log;

@Log
public class DatabaseInitializer implements InitializingBean {

    @Value("${spring.datasource.initial-script:schema-main.sql}")
    public String initialScript;


    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcTemplate.execute(new ConnectionCallback<Void>() {
            @Override
            @Nullable
            public Void doInConnection(@NonNull Connection con) throws SQLException, DataAccessException {
                var resource = new ClassPathResource(initialScript);
                log.info("Loading scripts: " + resource.getFilename());
                ScriptUtils.executeSqlScript(con, resource);
                return null;
            }
        });

        Long appUserCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM invoices.app_user", Long.class);
        if (appUserCount != null && appUserCount.longValue() == 0) {
            jdbcTemplate.update("""
                INSERT INTO invoices.app_user (first_name, last_name, email, password, admin, enabled)
                VALUES ('Admin', 'istrator', 'admin@fele-systems.com', ?, true, true)
                """.stripIndent(), passwordEncoder.encode("12345678"));
        }
    }

}
