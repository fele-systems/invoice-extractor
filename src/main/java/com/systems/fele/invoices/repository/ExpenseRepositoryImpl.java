package com.systems.fele.invoices.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.Installment;

@Repository
public class ExpenseRepositoryImpl implements ExpenseRepository {

    private final JdbcTemplate jdbcTemplate;

    public ExpenseRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ExpenseEntity> findByInvoiceIdAndLocalId(long invoiceId, long localId) {
        return jdbcTemplate
                .query("SELECT * FROM invoices.expense WHERE invoice_id = ? AND local_id = ?", new ExpenseRowMapper(),
                        invoiceId, localId)
                .stream()
                .findFirst();
    }

    @Override
    public void saveAll(List<ExpenseEntity> expenseEntities) {
        for (var e : expenseEntities) {
            save(e);
        }
    }

    @Override
    public ExpenseEntity save(ExpenseEntity expense) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            var stmt = conn.prepareStatement(
                    "INSERT INTO invoices.expense (amount, date, description, installment_no, total_installments, local_id, invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            stmt.setBigDecimal(1, expense.getAmount());
            stmt.setDate(2, Date.valueOf(expense.getDate()));
            stmt.setString(3, expense.getDescription());
            stmt.setInt(4, expense.getInstallment().getInstallmentNo());
            stmt.setInt(5, expense.getInstallment().getTotalInstallments());
            stmt.setLong(6, expense.getLocalId());
            stmt.setLong(7, expense.getInvoiceId());

            return stmt;
        }, keyHolder);

        var keys = keyHolder.getKeys();
        if (keys == null)
            throw new RuntimeException("There was an error retrieving the generated key!");

        return ExpenseEntity.builder()
                .id(((Long) keys.get("id")).longValue())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .description(expense.getDescription())
                .installment(expense.getInstallment())
                .localId(expense.getLocalId())
                .invoiceId(expense.getInvoiceId())
                .build();
    }

    @Override
    public void delete(long expenseId) {
        jdbcTemplate.update("DELETE FROM invoices.expense WHERE id = ?", expenseId);
    }

    @Override
    public long findLatestLocalId(long invoiceId) {
        var max = jdbcTemplate.queryForObject("SELECT MAX(local_id) FROM invoices.expense WHERE invoice_id = ?;",
                Long.class, invoiceId);
        if (max == null)
            return 0l;
        else
            return max.longValue();
    }

    @Override
    public List<ExpenseEntity> findExpensesByInvoiceId(long invoiceId) {
        return jdbcTemplate.query("SELECT * FROM invoices.expense WHERE invoice_id = ? ORDER BY local_id",
                new ExpenseRowMapper(), invoiceId);
    }

    @Override
    public void update(long expenseId,
            Optional<BigDecimal> optAmount,
            Optional<LocalDate> optDate,
            Optional<String> optDesc,
            Optional<Installment> optInstal) {
        var params = new MapSqlParameterSource();
        optAmount.ifPresent(v -> params.addValue("amount", v));
        optDate.ifPresent(v -> params.addValue("date", Date.valueOf(v)));
        optDesc.ifPresent(v -> params.addValue("description", v));
        optInstal.ifPresent(v -> {
            params.addValue("installment_no", v.getInstallmentNo());
            params.addValue("total_installments", v.getTotalInstallments());
        });

        if (params.getParameterNames().length == 0)
            return;

        // Build the SET expression based on which fields where present
        var sql = "UPDATE invoices.expense SET " + Arrays.asList(params.getParameterNames())
                .stream()
                .map(n -> String.format("%s=:%s", n, n))
                .collect(Collectors.joining(", ")) + " WHERE id = :expense_id";

        // Add the last value to the WHERE expression
        params.addValue("expense_id", expenseId);

        jdbcTemplate.update(sql, params);
    }
    // TODO:MEDIUM De-duplicate this method
    @Override
    public void update(long invoiceId, long expenseId, Optional<BigDecimal> optAmount, Optional<LocalDate> optDate,
            Optional<String> optDesc, Optional<Installment> optInstal) {
        var params = new MapSqlParameterSource();
        optAmount.ifPresent(v -> params.addValue("amount", v));
        optDate.ifPresent(v -> params.addValue("date", Date.valueOf(v)));
        optDesc.ifPresent(v -> params.addValue("description", v));
        optInstal.ifPresent(v -> {
            params.addValue("installment_no", v.getInstallmentNo());
            params.addValue("total_installments", v.getTotalInstallments());
        });

        if (params.getParameterNames().length == 0)
            return;

        // Build the SET expression based on which fields where present
        var sql = "UPDATE invoices.expense SET " + Arrays.asList(params.getParameterNames())
                .stream()
                .map(n -> String.format("%s=:%s", n, n))
                .collect(Collectors.joining(", ")) + " WHERE invoice_id = :invoice_id AND local_id = :local_id";

        // Add the values to the WHERE expression
        params.addValue("invoice_id", invoiceId);
        params.addValue("local_id", expenseId);

        jdbcTemplate.update(sql, params);
    }

}
