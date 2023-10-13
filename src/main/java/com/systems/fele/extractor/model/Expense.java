package com.systems.fele.extractor.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a expense inside an invoice
 */
public class Expense {

    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private Installment installment;
    
    public Expense(BigDecimal amount, String description, LocalDate date, Installment installment) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.installment = installment;
    }

    /**
     * Getter for amount. Always in BRL.
     * @return value
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Getter for description
     * @return value
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for date
     * @return value
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Getter for installment.
     * @return installment number or 0 if none
     */
    public Installment getInstallment() {
        return installment;
    }

    @Override
    public String toString() {
        return "Expense [amount=" + amount + ", description=" + description + ", date=" + date + ", installment="
                + installment + "]";
    }

    
}
