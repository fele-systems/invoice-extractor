package com.systems.fele.extractor.banks;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import com.systems.fele.common.strings.Strings;
import com.systems.fele.common.time.Months;
import com.systems.fele.common.util.Unchecked;
import com.systems.fele.extractor.exception.BadInvoiceException;
import com.systems.fele.extractor.exception.InvoiceFormatException;
import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.Installment;
import com.systems.fele.invoices.entity.InvoiceEntity;

public class NuBank implements Extractor {

    @Override
    public InvoiceEntity extract(LineStream stream) {

        if (!stream.find(line -> line.startsWith("Data do vencimento: "))) {
            throw new InvoiceFormatException("Could not find invoice due date");
        }

        var dueDateStr = stream.getLine().substring(20);

        var day = Integer.parseInt(dueDateStr.substring(0, 2));
        var month = Strings.begin(dueDateStr)
                .skip(3)
                .slice().takeWhile(Character::isAlphabetic)
                .map(Months::monthStrToInt);
        var year = Strings.rbegin(dueDateStr)
                .slice()
                .takeWhile(Character::isDigit)
                .rev()
                .toInt();

        var dueDate = LocalDate.of(year, month, day);

        var startOfExpenses = Pattern.compile("^TRANSAÇÕES DE \\d\\d \\w{3} A \\d\\d \\w{3} VALORES EM R\\$$");
        var installmentFormat = Pattern.compile("^- \\d+/\\d+$");
        var isExpenseFormat = Pattern.compile("^\\d\\d \\w{3} .*\\d+,\\d\\d$");
        var realFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.forLanguageTag("pt-BR")));
        realFormat.setParseBigDecimal(true);
        var expenses = new ArrayList<ExpenseEntity>();
        
        while (stream.find(line -> startOfExpenses.matcher(line).matches())) {
            String currentExpense = stream.advanceAndGet();

            while (isExpenseFormat.matcher(currentExpense).matches()) {
                var slices = Strings.begin(currentExpense)
                        .slice().take(2);
                var expenseDay = slices.toInt();

                slices = slices.sliceEOFAsIndex()
                        .skip(1)
                        .slice().take(3);
                var expenseMonth = slices.map(Months::monthStrToInt);

                var expenseDate = LocalDate.of(year, expenseMonth, expenseDay);

                var descriptionBegin = slices.sliceEOFAsIndex();

                // Start from the end to get amount and, optionally, installments
                slices = Strings.rbegin(currentExpense)
                        .slice().takeWhile(ch -> ch != ' ');

                var expenseValue = (BigDecimal) slices.rev().map(Unchecked.function(realFormat::parse));

                var description = descriptionBegin.sliceToEOF()
                        .take(-slices.length());

                slices = slices.sliceEOFAsIndex()
                        .skip(1)
                        .slice().takeWhile(ch -> ch != '-').take(1);

                Installment installment = Installment.NULL;

                if (installmentFormat.matcher(slices.rev().toString()).matches()) {
                    // Why matches twice !?!?
                    if (slices.sliceEOF() != slices.begin().EOF() && installmentFormat.matcher(slices.rev().toString()).matches() ) {
                        // It has installments
                        var result = slices.take(-2).rev().toString().split("/", 2);
                        if (result.length != 2) throw new BadInvoiceException("Detected installment did not had two value: %s. Parsing %s".formatted(slices.rev(), currentExpense));

                        try {
                            installment = new Installment(Integer.parseInt(result[1]), Integer.parseInt(result[0]));
                        } catch (NumberFormatException e) {
                            throw new BadInvoiceException("Detected installment is not convertible to int: %s. Parsing %s".formatted(slices.rev(), currentExpense), e);
                        }
                        
                        description = description.take(-slices.length() - 2);
                    }
                }

                expenses.add(new ExpenseEntity(null, expenseValue, description.toString().trim(), expenseDate, installment));
                stream.mark();
                currentExpense = stream.advanceAndGet();
            }
        }

        if (stream.eof()) stream.rollback();
        System.out.println("Unmatched line: " + stream.getLine());

        return new InvoiceEntity(LocalDate.from(dueDate), expenses);
    }

    @Override
    public boolean isExtractable(LineStream stream) {
        stream.skip(6);
        return stream.getAndAdvance().startsWith("Olá, ") &&
            stream.getLine().equals("Esta é a sua fatura de");
    }    
}
