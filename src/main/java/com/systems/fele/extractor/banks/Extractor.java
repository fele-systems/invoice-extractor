package com.systems.fele.extractor.banks;

import com.systems.fele.invoices.dto.CreateInvoiceRequest;

public interface Extractor {
    CreateInvoiceRequest extract(LineStream stream);

    boolean isExtractable(LineStream stream);
}
