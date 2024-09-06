package com.systems.fele.extractor.banks;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.systems.fele.common.strings.Slice;
import com.systems.fele.common.strings.Strings;
import com.systems.fele.common.time.Months;
import com.systems.fele.common.util.Unchecked;
import com.systems.fele.extractor.exception.BadInvoiceException;
import com.systems.fele.extractor.exception.InvoiceFormatException;
import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
import com.systems.fele.invoices.entity.Installment;

public class NuBank implements Extractor {

    final Pattern startOfExpenses = Pattern.compile("^TRANSAÇÕES DE \\d\\d \\w{3} A \\d\\d \\w{3} VALORES EM R\\$$", Pattern.CANON_EQ);
    final Pattern installmentFormat = Pattern.compile("^- \\d+/\\d+$");
    final Pattern isExpenseFormat = Pattern.compile("^\\d\\d \\w{3} .*\\d+,\\d\\d$");
    final Pattern isInternationalExpensePt1Format = Pattern.compile("^\\d\\d \\w{3} .*$");
    // CUR 1.000,00
    final Pattern isInternationalExpensePt2Format = Pattern.compile("^\\w{3} (\\d+\\.)?\\d+,\\d{2}$");
    // Conversão: USD 1 = R$ 5,15
    final Pattern isInternationalExpensePt3Format = Pattern.compile("^Conversão: .*$", Pattern.CANON_EQ);


    @Override
    public CreateInvoiceRequest extract(LineStream stream) {

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
        var expenses = new ArrayList<CreateExpenseRequest>();
        
        // Loop through pages
        while (stream.find(line -> startOfExpenses.matcher(line).matches())) {
            parsePage(stream, year, expenses);
        }

        if (stream.eof()) stream.rollback();

        return new CreateInvoiceRequest(LocalDate.from(dueDate), tidyExpenses(expenses));
    }

    private Optional<String> tryGetNextExpense(LineStream stream) {
        if (isExpenseFormat.matcher(stream.getLine()).matches()) {
            return Optional.of(stream.getLine());
        } else if (isInternationalExpensePt1Format.matcher(stream.getLine()).matches() &&
                stream.peekNext(1).map(isInternationalExpensePt2Format::matcher).map(Matcher::matches).orElse(false) &&
                stream.peekNext(2).map(isInternationalExpensePt3Format::matcher).map(Matcher::matches).orElse(false) &&
                stream.peekNext(3).isPresent()
        ) {
            var pt1 = stream.getLine();
            stream.skip(3);
            return Optional.of(pt1 + " " + stream.getLine());
        } else {
            return Optional.empty();
        }
    }

    private void parsePage(LineStream stream, int year, ArrayList<CreateExpenseRequest> expenses) {
        var reaisFormat = new DecimalFormat("#,#.##", new DecimalFormatSymbols(Locale.forLanguageTag("pt-BR")));
        reaisFormat.setParseBigDecimal(true);

        stream.skip(1);
        Optional<String> possibleExpense;
        while ( (possibleExpense = tryGetNextExpense(stream)).isPresent() ) {
            String currentExpense = possibleExpense.get();
            // The first two chars are the day
            var slices = Strings.begin(currentExpense)
                    .slice().take(2);
            var expenseDay = slices.toInt();

            // Skip a space and take the next three characters that
            // contain the month
            slices = slices.sliceEOFAsIndex()
                    .skip(1)
                    .slice().take(3);
            var expenseMonth = slices.map(Months::monthStrToInt);

            var expenseDate = LocalDate.of(year, expenseMonth, expenseDay);

            // After parsing the date, the next field is the description
            // but to get the end of the description, we need to parse it
            // backwards. So here we just save the begining
            var descriptionBegin = slices.sliceEOFAsIndex();

            // Start from the end to get amount and, optionally, installments
            // To parse amount, just collect chars until a space
            slices = Strings.rbegin(currentExpense)
                    .slice().takeWhile(ch -> ch != ' ');

            var expenseValue = (BigDecimal) slices.rev().map(Unchecked.function(reaisFormat::parse));

            // To find the end of description, just get the rest of the
            // slice and remove the last chars by the number of chars
            // extracted of amount
            Slice description = descriptionBegin.sliceToEOF()
                    .take(-slices.length());

            // slices here hold the amount. Keep reading it backwards
            // until we find a dash, which could mean this expense was purchased in installments
            slices = slices.sliceEOFAsIndex()
                    .skip(1)
                    .slice().takeWhile(ch -> ch != '-').take(1);

            Installment installment = Installment.NULL;

            // Test if the dash really means a installment or just part of the description
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

            var expense = new CreateExpenseRequest(
                    expenseValue,
                    description.toString().trim(),
                    expenseDate,
                    installment
            );

            expenses.add(expense);
            stream.mark();
            stream.skip(1);
        }
    }

    @Override
    public boolean isExtractable(LineStream stream) {
        stream.skip(6);
        return stream.getAndAdvance().startsWith("Olá, ") &&
            stream.getLine().equals("Esta é a sua fatura de");
    }

    /**
     * Removes previous invoices payments and detect expenses that are
     * credit.
     * @param expenses List of expenses
     * @return tidied expenses
     */
    private List<CreateExpenseRequest> tidyExpenses(List<CreateExpenseRequest> expenses) {
        var tidy = new ArrayList<CreateExpenseRequest>();
        for (var ex : expenses) {
            if (ex.getDescription().startsWith("Pagamento em ")) continue;

            if (ex.getDescription().startsWith("Desconto Antecipação ")
                    || ex.getDescription().startsWith("Estorno de")
                ) {
                tidy.add(new CreateExpenseRequest(
                        ex.getAmount().negate(),
                        ex.getDescription(),
                        ex.getDate(),
                        ex.getInstallment()
                ));
            } else {
                tidy.add(ex);
            }
        }
        return tidy;
    }
}
