package com.systems.fele.invoices.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
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
    
        var expenseEntities = new ArrayList<ExpenseEntity>();
        long localIdCounter = 1;
        var invoiceEntity = new InvoiceEntity(appUser, invoiceRequest.getDueDate(), expenseEntities);

        if (appUser != null)
            invoiceEntity = invoiceRepository.save(invoiceEntity);
        
        for (var expenseRequest : invoiceRequest.getExpenses()) {
            expenseEntities.add(ExpenseEntity.builder()
                    .localId(localIdCounter++)
                    .invoice(invoiceEntity)
                    .amount(expenseRequest.getAmount())
                    .description(expenseRequest.getDescription())
                    .date(expenseRequest.getDate())
                    .installment(expenseRequest.getInstallment())
                    .build());
        }

        if (appUser != null)
            expenseRepository.saveAll(expenseEntities);

        return invoiceEntity;
    }

    @Override
    public List<InvoiceEntity> listInvoices(long appUserId) {
        // TODO Maybe there's a way to find by user id
        return invoiceRepository.findByAppUser(appUserService.getUserById(appUserId));
    }

    @Override
    @NonNull
    public InvoiceEntity getInvoice(long invoiceId) {
        var invoiceOpt = invoiceRepository.findById(invoiceId);
        
        invoiceOpt.ifPresent(invoice -> invoice.getExpenses().sort(Comparator.comparing(ExpenseEntity::getLocalId)));

        return invoiceOpt.orElseThrow();
    }

    @SuppressWarnings("null")
    @Override
    public ExpenseEntity createExpense(long invoiceId, CreateExpenseRequest expenseRequest) {
        var invoice = getInvoice(invoiceId);

        long nextLocalId = invoice.getExpenses().stream()
                .sorted(Comparator.comparingLong(ExpenseEntity::getLocalId).reversed())
                .findFirst()
                .orElse(ExpenseEntity.builder().localId(0).build()).getLocalId() + 1;
                

        var expense = ExpenseEntity.builder()
            .localId(nextLocalId)
            .invoice(invoice)
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

        invoiceRepository.delete(invoiceEntity);
        return invoiceEntity;
    }

    @Override
    public ExpenseEntity getExpense(long invoiceId, long expenseLocalId) {
        return expenseRepository.findByInvoiceIdAndLocalId(invoiceId, expenseLocalId)
                .orElseThrow();
    }

    @Override
    public ExpenseEntity updateExpense(long invoiceId, long expenseId, UpdateExpenseRequest expenseRequest) {
        var currentExpense = getExpense(invoiceId, expenseId);

        if (expenseRequest.getAmount() != null)
            currentExpense.setAmount(expenseRequest.getAmount());

        if (expenseRequest.getDate() != null)
            currentExpense.setDate(expenseRequest.getDate());

        if (expenseRequest.getDescription() != null)
            currentExpense.setDescription(expenseRequest.getDescription());

        if (expenseRequest.getInstallment() != null)
            currentExpense.setInstallment(expenseRequest.getInstallment());

        return expenseRepository.save(currentExpense);

    }

    @Override
    public ExpenseEntity deleteExpense(long invoiceId, long expenseId) {
        var expense = getExpense(invoiceId, expenseId);
        expenseRepository.delete(expense);
        return expense;
    }
    
}
