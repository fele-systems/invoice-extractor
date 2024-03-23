package com.systems.fele.invoices.service;

import java.util.List;

import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.InvoiceEntity;
import com.systems.fele.users.entity.AppUser;

public interface InvoiceService {
    
    InvoiceEntity createInvoice(AppUser appUser, CreateInvoiceRequest invoiceRequest);

    InvoiceEntity createInvoice(long appUserId, CreateInvoiceRequest invoiceRequest);

    List<InvoiceEntity> listInvoices(long appUserId);

    InvoiceEntity getInvoice(long invoiceId);

    ExpenseEntity createExpense(long invoiceId, CreateExpenseRequest expenseRequest);
}
