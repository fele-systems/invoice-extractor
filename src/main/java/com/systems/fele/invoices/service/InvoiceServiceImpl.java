package com.systems.fele.invoices.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
import com.systems.fele.invoices.dto.ShortInvoiceDto;
import com.systems.fele.invoices.dto.UpdateExpenseRequest;
import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.InvoiceEntity;
import com.systems.fele.invoices.repository.ExpenseRepository;
import com.systems.fele.invoices.repository.InvoiceRepository;
import com.systems.fele.users.entity.AppUser;
import com.systems.fele.users.service.AppUserService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AppUserService appUserService;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public InvoiceServiceImpl(AppUserService appUserService, InvoiceRepository invoiceRepository, ExpenseRepository expenseRepository) {
        this.appUserService = appUserService;
        this.invoiceRepository = invoiceRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    public InvoiceEntity createInvoice(long appUserId, CreateInvoiceRequest invoiceRequest) {
        return createInvoice(appUserService.getUserById(appUserId), invoiceRequest);
    }

    @Override
    public InvoiceEntity createInvoice(AppUser appUser, CreateInvoiceRequest invoiceRequest) {
    
        long localIdCounter = 1;
        var invoiceEntity = new InvoiceEntity(appUser == null ? 0 : appUser.getId(), invoiceRequest.getDueDate(), null);
        
        if (appUser != null)
            invoiceEntity = invoiceRepository.save(invoiceEntity);
        
        invoiceEntity.setExpenses(new ArrayList<>());

        for (var expenseRequest : invoiceRequest.getExpenses()) {
            var expense = ExpenseEntity.builder()
                .localId(localIdCounter++)
                .invoiceId(invoiceEntity.getId())
                .amount(expenseRequest.getAmount())
                .description(expenseRequest.getDescription())
                .date(expenseRequest.getDate())
                .installment(expenseRequest.getInstallment())
                .build();

            if (appUser != null)
                expense = expenseRepository.save(expense);

            invoiceEntity.getExpenses().add(expense);
        }

        return invoiceEntity;
    }

    @Override
    public List<InvoiceEntity> listInvoices(long appUserId) {
        var invoices = invoiceRepository.findInvoicesForUserId(appUserId);
        for (var invoice : invoices) {
            invoice.setExpenses(
                expenseRepository.findExpensesByInvoiceId(invoice.getId())
            );
        }
        return invoices;
    }

    @Override
    @NonNull
    public InvoiceEntity getInvoice(long invoiceId) {
        var invoiceOpt = invoiceRepository.findById(invoiceId);
        
        invoiceOpt.ifPresent(invoice -> invoice.setExpenses(expenseRepository.findExpensesByInvoiceId(invoiceId)));

        return invoiceOpt.orElseThrow();
    }

    

    @SuppressWarnings("null")
    @Override
    public ExpenseEntity createExpense(long invoiceId, CreateExpenseRequest expenseRequest) {
        var invoice = getInvoice(invoiceId);

        long nextLocalId = expenseRepository.findLatestLocalId(invoiceId) + 1;

        var expense = ExpenseEntity.builder()
            .localId(nextLocalId)
            .invoiceId(invoice.getId())
            .amount(expenseRequest.getAmount())
            .description(expenseRequest.getDescription())
            .date(expenseRequest.getDate())
            .installment(expenseRequest.getInstallment())
            .build();

        return expenseRepository.save(expense);
    }

    @Override
    public InvoiceEntity deleteInvoice(long invoiceId) {
        var invoiceEntity = getInvoice(invoiceId);

        invoiceRepository.delete(invoiceEntity.getId());
        return invoiceEntity;
    }

    @Override
    public ExpenseEntity getExpense(long invoiceId, long expenseLocalId) {
        return expenseRepository.findByInvoiceIdAndLocalId(invoiceId, expenseLocalId)
                .orElseThrow();
    }

    @Override
    public ExpenseEntity updateExpense(long invoiceId, long localId, UpdateExpenseRequest expenseRequest) {
        expenseRepository.update(
            invoiceId, localId,
            Optional.ofNullable(expenseRequest.getAmount()),
            Optional.ofNullable(expenseRequest.getDate()),
            Optional.ofNullable(expenseRequest.getDescription()),
            Optional.ofNullable(expenseRequest.getInstallment()));

        return expenseRepository.findByInvoiceIdAndLocalId(invoiceId, localId).get();
    }

    @Override
    public ExpenseEntity deleteExpense(long invoiceId, long localExpenseId) {
        var expense = getExpense(invoiceId, localExpenseId);
        expenseRepository.delete(expense.getId());
        return expense;
    }

    @Override
    public List<ShortInvoiceDto> getShortInvoicesForUserId(long appUserId) {
        return invoiceRepository.getShortInvoicesForUserId(appUserId);
    }
    
}
