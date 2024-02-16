package com.systems.fele.extractor.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.systems.fele.extractor.banks.PdfInvoiceResource;
import com.systems.fele.extractor.model.Expense;
import com.systems.fele.extractor.model.Invoice;
import com.systems.fele.extractor.service.ExtractorService;
import com.systems.fele.users.model.AppUser;
import com.systems.fele.users.repository.AppUserRepository;

@RestController

public class ExtractorController {

    private final ExtractorService extractorService;
    private final AppUserRepository userRepository;

    @Autowired
    public ExtractorController(ExtractorService extractorService,
            AppUserRepository userRepository) {
        this.extractorService = extractorService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "rest/api/extract", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public Invoice extract(@RequestPart("document") MultipartFile file,
            @Nullable @RequestPart("password") String password,
            @Nullable @RequestPart("hint") String hint,
            @Nullable @RequestPart("user") Long userId) throws IOException {
        var resource = new PdfInvoiceResource(file.getInputStream(), Optional.ofNullable(password));

        AppUser appUser = null;
        if (userId != null) {
            appUser = userRepository.findById(userId)
                .orElseThrow();
        }

        var invoiceEntity = (hint != null) ? extractorService.extractWithHint(resource, hint, appUser)
                : extractorService.extract(resource, appUser);
        
        var expenses = invoiceEntity.getExpenses().stream().map(Expense::fromEntity).toList();
        
        return new Invoice((long)invoiceEntity.getId(), invoiceEntity.getDueDate(), expenses);
    }
}
