package com.systems.fele.extractor.banks;

import com.systems.fele.invoices.entity.InvoiceEntity;

public interface Extractor {
    InvoiceEntity extract(LineStream stream);

    boolean isExtractable(LineStream stream);
}
