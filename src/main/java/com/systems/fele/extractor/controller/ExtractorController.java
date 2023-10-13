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
import com.systems.fele.extractor.model.Invoice;
import com.systems.fele.extractor.service.ExtractorService;

@RestController
public class ExtractorController {

    private final ExtractorService extractorService;

    @Autowired
    public ExtractorController(ExtractorService extractorService) {
        this.extractorService = extractorService;
    }

    @PostMapping(value = "/extract",
        consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE })
    public Invoice extract(@RequestPart("document") MultipartFile file, @Nullable @RequestPart("password") String password, @Nullable @RequestPart("hint") String hint) throws IOException {
        var resource = new PdfInvoiceResource(file.getInputStream(), Optional.ofNullable(password));

        if (hint != null)
            return extractorService.extractWithHint(resource, hint);
        else
            return extractorService.extract(resource);
    }
}
