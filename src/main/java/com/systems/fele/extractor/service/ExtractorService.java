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
import com.systems.fele.extractor.entity.InvoiceEntity;
import com.systems.fele.extractor.exception.InvoiceFormatException;
import com.systems.fele.extractor.repository.InvoiceRepository;
import com.systems.fele.users.entity.AppUser;

@Service
public class ExtractorService {
    Map<String, Extractor> availableExtractors;

    @Autowired
    private InvoiceRepository tempInvoiceRepository;

    public ExtractorService() {
        availableExtractors = Map.of(
                "BANCO INTER", new BancoInter(),
                "NU BANK", new NuBank());
    }

    public Collection<String> getExtractorHints() {
        return Set.copyOf(availableExtractors.keySet());
    }

    public InvoiceEntity extract(InvoiceResource resource, AppUser appUser) {
        var lineStream = resource.loadAsLineStream();

        InvoiceEntity invoice = null;
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

        // create and save the invoice entity
        invoice.setAppUser(appUser);

        for (var expense : invoice.getExpenses())
            expense.setInvoice(invoice);

        return tempInvoiceRepository.save(invoice);
    }

    public InvoiceEntity extractWithHint(InvoiceResource resource, String bankHint, AppUser appUser) {
        var invoice = availableExtractors.get(bankHint).extract(resource.loadAsLineStream());

        // create and save the invoice entity
        invoice.setAppUser(appUser);

        for (var expense : invoice.getExpenses())
            expense.setInvoice(invoice);

        return tempInvoiceRepository.save(invoice);
    }

}
