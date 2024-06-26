package com.systems.fele.invoices.service;

import java.util.List;

import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
import com.systems.fele.invoices.dto.UpdateExpenseRequest;
import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.InvoiceEntity;
import com.systems.fele.users.entity.AppUser;

public interface InvoiceService {
    
    InvoiceEntity createInvoice(AppUser appUser, CreateInvoiceRequest invoiceRequest);

    InvoiceEntity createInvoice(long appUserId, CreateInvoiceRequest invoiceRequest);

    List<InvoiceEntity> listInvoices(long appUserId);

    InvoiceEntity getInvoice(long invoiceId);

    InvoiceEntity deleteInvoice(long invoiceId);

    ExpenseEntity createExpense(long invoiceId, CreateExpenseRequest expenseRequest);

    ExpenseEntity getExpense(long invoiceId, long expenseLocalId);

    ExpenseEntity updateExpense(long invoiceId, long expenseId, UpdateExpenseRequest expenseRequest);

    ExpenseEntity deleteExpense(long invoiceId, long expenseId);
}
