package com.systems.fele.invoices.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.Installment;

public class ExpenseRowMapper implements RowMapper<ExpenseEntity> {

    @Override
    @Nullable
    public ExpenseEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        return ExpenseEntity.builder()
            .id(rs.getLong("id"))
            .invoiceId(rs.getLong("invoice_id"))
            .localId(rs.getLong("local_id"))
            .amount(rs.getBigDecimal("amount"))
            .date(rs.getDate("date").toLocalDate())
            .description(rs.getString("description"))
            .installment(new Installment(
                rs.getInt("installment_no"),
                rs.getInt("total_installments")
            ))
            .build();
    }
    
}
