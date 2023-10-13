package com.systems.fele.extractor.banks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfInvoiceResource implements InvoiceResource {

    private final InputStream stream;
    private final String password;

    public PdfInvoiceResource(final InputStream stream, final Optional<String> password) {
        this.stream = stream;
        this.password = password.orElse(null);
    }

    @Override
    public LineStream loadAsLineStream() {
        try (PDDocument doc = password == null
            ? PDDocument.load(stream)
            : PDDocument.load(stream, password)) {
            var stripper = new PDFTextStripper();
            return new LineStream(stripper.getText(doc)
                    .lines()
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
