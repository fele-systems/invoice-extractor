package com.systems.fele.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import com.systems.fele.tests.data.TestData;


public class TestExtractors {
    
    private static Stream<Arguments> provideArgumentsToTestExtraction() throws FileNotFoundException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        var testData = TestData.load();
        var argumentsList = new ArrayList<Arguments>();
        
        for (var entry : testData.getExtractors().entrySet()) {
            var key = entry.getKey();
            var val = entry.getValue();

            if (val.isDisabled()) continue;

            var extractor = (Extractor) Class.forName(key).getConstructor().newInstance();
            var password = Optional.ofNullable(val.getPassword());
            for (var invoice : val.getFiles()) {
                argumentsList.add(Arguments.of(
                    new PdfInvoiceResource(new FileInputStream("invoices/" + invoice), password),
                    extractor
                ));
            }
        };

        return argumentsList.stream();
    }

    @ParameterizedTest(name = "Extracting {1}'s invoice")
    @MethodSource("provideArgumentsToTestExtraction")
    public void testExtraction(InvoiceResource InvoiceResource, Extractor extractor) {
        var lineStream = InvoiceResource.loadAsLineStream();
        System.out.println(extractor.extract(lineStream));
    }


}
