package com.systems.fele.extractor.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.systems.fele.extractor.banks.BancoInter;
import com.systems.fele.extractor.banks.Extractor;
import com.systems.fele.extractor.banks.InvoiceResource;
import com.systems.fele.extractor.banks.NuBank;
import com.systems.fele.extractor.exception.InvoiceFormatException;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
import com.systems.fele.invoices.entity.InvoiceEntity;
import com.systems.fele.invoices.service.InvoiceService;
import com.systems.fele.users.entity.AppUser;

@Service
public class ExtractorService {
    Map<String, Extractor> availableExtractors;

    private InvoiceService invoiceService;

    @Autowired
    public ExtractorService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;

        availableExtractors = Map.of(
                "BANCO INTER", new BancoInter(),
                "NU BANK", new NuBank());
    }

    public Collection<String> getExtractorHints() {
        return Set.copyOf(availableExtractors.keySet());
    }

    public InvoiceEntity extract(InvoiceResource resource, AppUser appUser) {
        var lineStream = resource.loadAsLineStream();

        CreateInvoiceRequest invoice = null;
        for (var entry : availableExtractors.entrySet()) {
            var extractor = entry.getValue();
            if (extractor.isExtractable(lineStream)) {
                invoice = extractor.extract(lineStream);
                break;
            }
        }

        // no extractor available for this invoice
        if (invoice == null) {
            throw new InvoiceFormatException("Unreconized format");
        }

        return invoiceService.createInvoice(appUser, invoice);
    }

    public InvoiceEntity extractWithHint(InvoiceResource resource, String bankHint, AppUser appUser) {
        var invoice = availableExtractors.get(bankHint).extract(resource.loadAsLineStream());


        return invoiceService.createInvoice(appUser, invoice);
    }

}
