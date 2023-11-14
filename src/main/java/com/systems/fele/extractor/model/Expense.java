package com.systems.fele.extractor.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a expense inside an invoice
 */
@Getter
@AllArgsConstructor
public class Expense {
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private Installment installment;  
}
