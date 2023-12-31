package com.systems.fele.extractor.banks;

import java.io.InputStream;

import com.systems.fele.extractor.model.Invoice;

public interface Extractor {
    Invoice extract(LineStream stream);

    boolean isExtractable(LineStream stream);
}
