package com.systems.fele.extractor.service;

import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.systems.fele.extractor.banks.BancoInter;
import com.systems.fele.extractor.banks.Extractor;
import com.systems.fele.extractor.banks.InvoiceResource;
import com.systems.fele.extractor.exception.InvoiceFormatException;
import com.systems.fele.extractor.model.Invoice;

@Service
public class ExtractorService {
    Map<String, Extractor> availableExtractors;

    public ExtractorService() {
        availableExtractors = Map.of(
            "BANCO INTER", new BancoInter()
        );
    }

    public Invoice extract(InvoiceResource resource) {
        var lineStream = resource.loadAsLineStream();

        for (var entry : availableExtractors.entrySet()) {
            var extractor = entry.getValue();
            if (extractor.isExtractable(lineStream)) {
                return extractor.extract(lineStream);
            }
        }

        throw new InvoiceFormatException("Unreconized format");
    }

    public Invoice extractWithHint(InvoiceResource resource, String bankHint) {
        return availableExtractors.get(bankHint).extract(resource.loadAsLineStream());
    }
}
