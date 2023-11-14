package com.systems.fele.extractor.banks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Locale;

import com.systems.fele.common.util.StringUtils;
import com.systems.fele.extractor.exception.InvoiceFormatException;
import com.systems.fele.extractor.model.Invoice;

public class NuBank implements Extractor {

    @Override
    public Invoice extract(LineStream stream) {

        if (!stream.find(line -> line.startsWith("Data do vencimento: "))) {
            throw new InvoiceFormatException("Could not find invoice due date");
        }

        var dueDateStr = stream.getLine().substring(20);

        var dueDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("pt-BR"));

        var day = Integer.parseInt(dueDateStr.substring(0, 2));
        var month = StringUtils.rBegin(dueDateStr)
                .skip(3)
                .slice().takeWhile(Character::isAlphabetic)
                .map(NuBank::monthStrToInt);
        var year = StringUtils.rEnd(dueDateStr)
                .slice()
                .takeWhile(Character::isDigit)
                .toInt();

        var dueDate = LocalDate.of(year, month, day);



        return new Invoice(LocalDate.from(dueDate), Arrays.asList());
    }

    @Override
    public boolean isExtractable(LineStream stream) {
        stream.skip(6);
        return stream.getAndAdvance().startsWith("Olá, ") &&
            stream.getLine().equals("Esta é a sua fatura de");
    }


    
    private static int monthStrToInt(String monthStr) {
        return switch (monthStr) {
            case "JAN"
        };
    }
    
}
