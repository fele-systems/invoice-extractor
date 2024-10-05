package com.systems.fele.invoices.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.systems.fele.invoices.dto.ShortInvoiceDto;
import com.systems.fele.invoices.entity.InvoiceEntity;

@Repository
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InvoiceRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<InvoiceEntity> findInvoicesForUserId(long appUserId) {
        return jdbcTemplate.query("SELECT * FROM invoices.invoice WHERE appuser_id = ?", new InvoiceRowMapper(), appUserId);
    }

    @Override
    public InvoiceEntity save(InvoiceEntity invoiceEntity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            var stmt = conn.prepareStatement(
            "INSERT INTO invoices.invoice (due_date, appuser_id) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );

            stmt.setDate(1, Date.valueOf(invoiceEntity.getDueDate()));
            stmt.setLong(2, invoiceEntity.getAppUserId());

            return stmt;
        }, keyHolder);

        var keys = keyHolder.getKeys();
        if (keys == null) {
            throw new RuntimeException("There was an error obaining the last inserted user!");
        } else {
            return new InvoiceEntity(
                ((Long) keys.get("id")).longValue(),
                invoiceEntity.getAppUserId(),
                invoiceEntity.getDueDate(),
                new ArrayList<>()
            );
        }
    }

    @Override
    public Optional<InvoiceEntity> findById(long invoiceId) {
        return jdbcTemplate.query("SELECT * FROM invoices.invoice WHERE id = ?", new InvoiceRowMapper(), invoiceId)
            .stream()
            .findFirst();
        
    }

    @Override
    public void delete(long invoiceId) {
        jdbcTemplate.update("DELETE FROM invoices.invoice WHERE id = ?", invoiceId);
    }

    @Override
    public List<ShortInvoiceDto> getShortInvoicesForUserId(long appUserId) {
        var rowMapper = new RowMapper<ShortInvoiceDto>() {
            @Override @Nullable
            public ShortInvoiceDto mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                return new ShortInvoiceDto(
                    rs.getLong("id"),
                    rs.getDate("due_date").toLocalDate(),
                    rs.getInt("count"),
                    rs.getBigDecimal("sum")
                );
            }
        };

        return jdbcTemplate.query(
            """
            SELECT inv.id, inv.due_date, count(exp.local_id), sum(exp.amount)
            FROM invoices.invoice inv
                LEFT JOIN invoices.expense exp
                ON inv.id = exp.invoice_id
            WHERE inv.appuser_id = ?
            GROUP BY inv.id
            """,
            rowMapper,
            appUserId
        );
    }
    
}
