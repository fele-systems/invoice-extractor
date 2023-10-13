package com.systems.fele.extractor.banks;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import com.systems.fele.common.util.StringUtils;
import com.systems.fele.extractor.exception.InvoiceFormatException;
import com.systems.fele.extractor.model.Expense;
import com.systems.fele.extractor.model.Installment;
import com.systems.fele.extractor.model.Invoice;

public class BancoInter implements Extractor {

    private LocalDate parseExpenseDate(String expenseAsStr) {
        var day = Integer.parseInt(expenseAsStr.substring(0, 2));

        Month month = switch(expenseAsStr.substring(3, 6)) {
            case "jan" -> Month.JANUARY;
            case "fev" -> Month.FEBRUARY;
            case "mar" -> Month.MARCH;
            case "mai" -> Month.MAY;
            case "jun" -> Month.JUNE;
            case "jul" -> Month.JULY;
            case "ago" -> Month.AUGUST;
            case "set" -> Month.SEPTEMBER;
            case "out" -> Month.OCTOBER;
            case "nov" -> Month.NOVEMBER;
            case "dec" -> Month.DECEMBER;
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
        
        var installmentSectionIndex = StringUtils.indexOf(description, '\0');
        
        Installment installment = null;
        if (!installmentSectionIndex.isEOF()) {
            System.out.println(installmentSectionIndex.skip(1).skip("Parcela ".length()).slice());
            // Skip the next "Parcela " characters
            int instNo = Integer.parseInt(installmentSectionIndex.skip(1).skip("Parcela ".length())
                    .slice()
                    .take(2)
                    //.takeWhile(Character::isDigit)
                    .toString());

            int instTotal = Integer.parseInt(StringUtils.rEnd(description)
                    .skipTo(' ')
                    .rev()
                    .skip(1)
                    .slice()
                    .takeWhile(Character::isDigit)
                    .toString());
                

            description = description.substring(0, installmentSectionIndex.getIndex()-2);
            installment = new Installment(instNo, instTotal);
        }
        
        var numberFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.forLanguageTag("pt-BR")));
        numberFormat.setParseBigDecimal(true);
        try {
            return new Expense((BigDecimal) numberFormat.parse(amount), description, date, installment);
        } catch (ParseException e) {
            throw new InvoiceFormatException("Could not parse amount: " + amount);
        }
    }

    @Override
    public Invoice extract(LineStream stream) {
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
                    throw new InvoiceFormatException("Parsing: " + StringUtils.escape(asStr), e);
                }
                
            }
        }
        return invoice;
    }

    @Override
    public boolean isExtractable(LineStream stream) {
        
        return stream.getLine().startsWith("Oi, ");
        
    }
}
