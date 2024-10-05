package com.systems.fele.invoices.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.systems.fele.invoices.entity.InvoiceEntity;

public class InvoiceRowMapper implements RowMapper<InvoiceEntity> {

    @Override
    @Nullable
    public InvoiceEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        return new InvoiceEntity(
            rs.getLong("id"),
            rs.getLong("appuser_id"),
            rs.getDate("due_date").toLocalDate(),
            Arrays.asList());
    }
    
}
