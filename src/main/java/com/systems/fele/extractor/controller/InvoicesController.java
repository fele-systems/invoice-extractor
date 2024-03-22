package com.systems.fele.extractor.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.systems.fele.extractor.entity.InvoiceEntity;
import com.systems.fele.extractor.model.Expense;
import com.systems.fele.extractor.model.Invoice;
import com.systems.fele.extractor.model.ListInvoicesResponse;
import com.systems.fele.extractor.repository.InvoiceRepository;
import com.systems.fele.users.entity.AppUser;

@RestController
@RequestMapping("/rest/api/invoice")
public class InvoicesController {
    
    @Autowired InvoiceRepository invoiceRepository;

    @GetMapping(value = "list",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListInvoicesResponse listInvoices(@Nullable @RequestParam("from") String from,
            @Nullable @RequestParam("to") String to,
            @Nullable @RequestParam("expand_expenses") Boolean expandExpenses) {

        var dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        var fromDate = from == null ? null : LocalDate.from(dateTimeFormatter.parse(from));
        var toDate = to == null ? null : LocalDate.from(dateTimeFormatter.parse(to));
        
        var expand = expandExpenses != null && expandExpenses == true;
        
        final List<InvoiceEntity> invoiceEntities;
        AppUser user = null; // TODO: Add user session
        
        if (fromDate == null) {
            if (toDate == null)
                invoiceEntities = invoiceRepository.findByAppUser(user);
            else
                invoiceEntities = invoiceRepository.findByAppUserAndDueDateLessThanEqual(user, toDate);
        } else {
            if (toDate == null)
                invoiceEntities = invoiceRepository.findByAppUserAndDueDateGreaterThanEqual(user, fromDate);
            else
                invoiceEntities = invoiceRepository.findByAppUserAndDueDateBetween(user, fromDate, toDate);
        }

        return new ListInvoicesResponse(invoiceEntities.stream()
                .map(i -> new Invoice(i.getId(), i.getDueDate(), expand ? i.getExpenses().stream().map(Expense::fromEntity).toList() : null))
                .toList(), expand);
    }

}
