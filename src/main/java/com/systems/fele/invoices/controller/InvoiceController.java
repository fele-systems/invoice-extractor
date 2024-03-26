package com.systems.fele.invoices.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
import com.systems.fele.invoices.dto.ExpenseDto;
import com.systems.fele.invoices.dto.InvoiceDto;
import com.systems.fele.invoices.dto.ShortInvoiceDto;
import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.service.InvoiceService;
import com.systems.fele.users.service.AppUserService;

@RestController
@RequestMapping("/rest/api/invoices")
public class InvoiceController {
    
    private final AppUserService appUserService;
    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, AppUserService appUserService) {
        this.invoiceService = invoiceService;
        this.appUserService = appUserService;
    }

    @PostMapping
    public InvoiceDto create(@RequestBody CreateInvoiceRequest invoiceRequest) {
        return InvoiceDto.fromEntity(invoiceService.createInvoice(appUserService.loggedInUser(), invoiceRequest));
    }

    @GetMapping("/{id}")
    public InvoiceDto get(@PathVariable Long id) {
        return InvoiceDto.fromEntity(invoiceService.getInvoice(id));
    }

    @GetMapping()
    public List<ShortInvoiceDto> list() {
        return invoiceService.listInvoices(appUserService.loggedInUser().getId())
                .stream()
                .map(invoice -> new ShortInvoiceDto(
                        invoice.getId(),
                        invoice.getDueDate(),
                        invoice.getExpenses().size(),
                        invoice.getExpenses().stream()
                                .map(ExpenseEntity::getAmount)
                                .reduce(BigDecimal::add)
                                .orElse(BigDecimal.ZERO)))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public InvoiceDto deleteInvoice(@PathVariable Long id) {
        return InvoiceDto.fromEntity(invoiceService.deleteInvoice(id));
    }

    @PostMapping("/{id}/expenses")
    public ExpenseDto createExpense(@PathVariable Long id, @RequestBody CreateExpenseRequest expenseRequest) {
        return ExpenseDto.fromEntity(invoiceService.createExpense(id, expenseRequest));
    }
}
