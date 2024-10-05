package com.systems.fele.invoices.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.Installment;

public interface ExpenseRepository {
    public Optional<ExpenseEntity> findByInvoiceIdAndLocalId(long invoiceId, long localId);

    public List<ExpenseEntity> findExpensesByInvoiceId(long invoiceId);

    public void saveAll(List<ExpenseEntity> expenseEntities);

    public ExpenseEntity save(ExpenseEntity expense);

    public void update(long expenseId,
        Optional<BigDecimal> optAmount,
        Optional<LocalDate> optDate,
        Optional<String> optDesc,
        Optional<Installment> optInstal
    );

    public void update(long invoiceId, long expense_id,
        Optional<BigDecimal> optAmount,
        Optional<LocalDate> optDate,
        Optional<String> optDesc,
        Optional<Installment> optInstal
    );

    public void delete(long expenseId);

    /**
     * Returns the highest local id currently registered
     * by expenses of desired invoice
     * @param invoiceId Invoice id
     * @return The highest local id
     */
    public long findLatestLocalId(long invoiceId);
}
