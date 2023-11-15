package com.systems.fele.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.systems.fele.extractor.banks.BancoInter;
import com.systems.fele.extractor.banks.Extractor;
import com.systems.fele.extractor.banks.InvoiceResource;
import com.systems.fele.extractor.banks.NuBank;
import com.systems.fele.extractor.banks.PdfInvoiceResource;


public class TestExtractors {
    
    private static Stream<Arguments> provideArgumentsToTestExtraction() throws FileNotFoundException {
        return Stream.of(
            Arguments.of(
                new PdfInvoiceResource(new FileInputStream("invoices/inter-september.pdf"), Optional.of("<redacted>")),     
                new BancoInter()
            ),
            Arguments.of(
                new PdfInvoiceResource(new FileInputStream("invoices/nubank-october.pdf"), Optional.empty()),     
                new NuBank()
            )
        );
    }

    @ParameterizedTest(name = "Extracting {1}'s invoice")
    @MethodSource("provideArgumentsToTestExtraction")
    public void testExtraction(InvoiceResource InvoiceResource, Extractor extractor) {
        var lineStream = InvoiceResource.loadAsLineStream();
        extractor.extract(lineStream);
    }


}
