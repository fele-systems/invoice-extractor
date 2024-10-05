package com.systems.fele.invoices.repository;

import java.util.List;
import java.util.Optional;

import com.systems.fele.invoices.entity.InvoiceEntity;

public interface InvoiceRepository {
    
    List<InvoiceEntity> findInvoicesForUserId(long appUserId);

    InvoiceEntity save(InvoiceEntity invoiceEntity);

    Optional<InvoiceEntity> findById(long invoiceId);

    void delete(long invoiceId);

}
