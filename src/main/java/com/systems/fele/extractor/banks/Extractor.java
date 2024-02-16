package com.systems.fele.extractor.banks;

import com.systems.fele.extractor.entity.InvoiceEntity;

public interface Extractor {
    InvoiceEntity extract(LineStream stream);

    boolean isExtractable(LineStream stream);
}
