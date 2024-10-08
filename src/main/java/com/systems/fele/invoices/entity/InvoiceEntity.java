package com.systems.fele.invoices.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {
    private long id;

    long appUserId;

    LocalDate dueDate;

    List<ExpenseEntity> expenses;

    public BigDecimal getTotal() {
        return expenses.stream()
            .map(ExpenseEntity::getAmount)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
    }

    public InvoiceEntity(LocalDate dueDate, List<ExpenseEntity> expenses) {
        this(0, dueDate, expenses);
    }

    public InvoiceEntity(long appUserId, LocalDate dueDate, List<ExpenseEntity> expenses) {
        this.appUserId = appUserId;
        this.dueDate = dueDate;
        this.expenses = expenses;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        
        builder.append(dueDate.getMonth().name())
            .append("'s Invoice\n\n");
        
        var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        int dateColumnSize = 8;
        int descriptionColumnSize = 10;
        int amountColumnSize = 7;
        int installmentColumnSize = 4;

        for (var expense : expenses) {
            if (expense.getDescription().length() > descriptionColumnSize) {
                descriptionColumnSize = expense.getDescription().length();
            }
        }

        builder.append("    Date")
                .append("| ");
        appendPaddedLeft(builder, "Description", descriptionColumnSize)
            .append("| ");
        appendPaddedLeft(builder, "Amount", amountColumnSize)
            .append("| ");
        appendPaddedLeft(builder, "Ins.", installmentColumnSize)
            .append("\n");

        int lineLen = dateColumnSize + descriptionColumnSize + amountColumnSize + installmentColumnSize + 6; // +6 for '| '
        for (int i = 0; i < lineLen; i++) builder.append('-');
        builder.append('\n');
        var numberFormat = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.forLanguageTag("pt_BR")));
        for (var expense : expenses) {
            builder.append(dateFormatter.format(expense.getDate()))
                .append("| ");
            appendPaddedLeft(builder, expense.getDescription(), descriptionColumnSize)
                .append("| ");
            appendPaddedLeft(builder, numberFormat.format(expense.getAmount()), amountColumnSize)
                .append("| ");
            appendPaddedLeft(builder, String.valueOf(expense.getInstallment()), installmentColumnSize)
                .append("\n");
        }

        return builder.toString();
    }

    private static StringBuilder appendPaddedLeft(StringBuilder builder, String value, int maxSize) {
        int toPad = maxSize - value.length();
        if (toPad > 0) for (int i = 0; i < toPad; i++) builder.append(' ');
        for (int i = toPad; i < maxSize; i++) builder.append(value.charAt(i-toPad));
        return builder;
    }
}
