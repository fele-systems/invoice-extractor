package com.systems.fele;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class BancoInter extends PdfExtractor {

    private LocalDate parseExpenseDate(String expenseAsStr) {
        var day = Integer.parseInt(expenseAsStr.substring(0, 2));

        Month month = switch(expenseAsStr.substring(3, 6)) {
            case "jan" -> month = Month.JANUARY;
            case "fev" -> month = Month.FEBRUARY;
            case "mar" -> month = Month.MARCH;
            case "mai" -> month = Month.MAY;
            case "jun" -> month = Month.JUNE;
            case "jul" -> month = Month.JULY;
            case "ago" -> month = Month.AUGUST;
            case "set" -> month = Month.SEPTEMBER;
            case "out" -> month = Month.OCTOBER;
            case "nov" -> month = Month.NOVEMBER;
            case "dec" -> month = Month.DECEMBER;
            default -> throw new InvoiceFormatException("Invalid expense date for: " + expenseAsStr);
        };

        var year = Integer.parseInt(expenseAsStr.substring(7, 11));

        return LocalDate.of(year, month, day);
    }

    private Expense parseExpense(String expenseAsStr) {
        var date = parseExpenseDate(expenseAsStr);

        var amountIndex = expenseAsStr.lastIndexOf("R$");

        // Check for returns and credits in for of '+ R$'
        if (expenseAsStr.charAt(amountIndex - 1) == ' ' &&
                expenseAsStr.charAt(amountIndex - 2) == '+')
            amountIndex -= 2;

        var amount = expenseAsStr.substring(expenseAsStr.lastIndexOf(" ")+1);

        
        var description = expenseAsStr.substring(12, amountIndex - 1);
        
        var installmentIndex = description.indexOf(0);
        int installment = 0;
        if (installmentIndex > 0) {
            int discardPrefix = installmentIndex + 1 + "Parcela ".length();
            int discardSufix = description.indexOf(" ", discardPrefix);
            installment = Integer.parseInt(description.substring(discardPrefix, discardSufix));
            description = description.substring(0, installmentIndex);
        }         
        System.out.println("Decimal sep: " + new DecimalFormatSymbols(Locale.forLanguageTag("pt-BR")).getDecimalSeparator());
        var numberFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.forLanguageTag("pt-BR")));
        numberFormat.setParseBigDecimal(true);
        try {
            return new Expense((BigDecimal) numberFormat.parse(amount), description, date, installment);
        } catch (ParseException e) {
            throw new InvoiceFormatException("Could not parse amount: " + amount);
        }
    }

    @Override
    protected Invoice extract(LineStream stream) {
        if (!stream.find("VENCIMENTO")) {
            throw new InvoiceFormatException("Could not find due date");
        }
        var dueDate = stream.advanceAndGet();
        var invoice = new Invoice(LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(dueDate)), new ArrayList<>());

        if (!stream.find("Despesas da fatura")) {
            throw new InvoiceFormatException("Could not find expenses list");
        }

        while (stream.find(line -> line.startsWith("CARTÃO"))) {
            var card = stream.getLine();
            var first4digits = card.substring(7, 12);
            var last4digits = card.substring(card.length()-4, card.length());
            System.out.printf("Card: %s **** **** %s%n", first4digits, last4digits);

            stream.skip(2);
            
            while (!stream.getLine().startsWith("VALOR TOTAL CARTÃO")) {
            // Parse expense
                String asStr = stream.getAndAdvance();
                try {
                    invoice.getExpenses().add(parseExpense(asStr));
                } catch (Exception e) {
                    throw new InvoiceFormatException("Parsing: " + asStr);
                }
                
            }
        }
        return invoice;
    }
}
