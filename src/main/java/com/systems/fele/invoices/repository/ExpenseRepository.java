package com.systems.fele.invoices.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systems.fele.invoices.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    public Optional<ExpenseEntity> findByInvoiceIdAndLocalId(long invoiceId, long localId);
}
