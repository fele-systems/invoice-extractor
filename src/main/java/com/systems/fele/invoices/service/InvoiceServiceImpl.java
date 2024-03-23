package com.systems.fele.invoices.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
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
    
        var invoiceEntity = new InvoiceEntity(appUser, invoiceRequest.getDueDate(), invoiceRequest.getExpenses()
                .stream()
                .map(expense -> new ExpenseEntity(
                        null,
                        expense.getAmount(),
                        expense.getDescription(),
                        expense.getDate(),
                        expense.getInstallment())
                )
                .collect(Collectors.toList()));
        
        return invoiceRepository.save(invoiceEntity);
    }

    @Override
    public List<InvoiceEntity> listInvoices(long appUserId) {
        // TODO Maybe there's a way to find by user id
        return invoiceRepository.findByAppUser(appUserService.getUserById(appUserId));
    }

    @Override
    public InvoiceEntity getInvoice(long invoiceId) {
        return invoiceRepository.findById(invoiceId).orElseThrow();
    }

    @Override
    public ExpenseEntity createExpense(long invoiceId, CreateExpenseRequest expenseRequest) {
        var invoice = getInvoice(invoiceId);

        return expenseRepository.save(new ExpenseEntity(
                invoice,
                expenseRequest.getAmount(),
                expenseRequest.getDescription(),
                expenseRequest.getDate(),
                expenseRequest.getInstallment())
        );
    }
    
}
