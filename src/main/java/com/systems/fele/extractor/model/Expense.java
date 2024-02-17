package com.systems.fele.extractor.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.systems.fele.extractor.entity.ExpenseEntity;
import com.systems.fele.extractor.model.Installment.InstallmentSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a expense inside an invoice
 */
@Getter
@AllArgsConstructor
public class Expense {
    private long id;
    
    private BigDecimal amount;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonSerialize(using = InstallmentSerializer.class)
    private Installment installment;

    public static Expense fromEntity(ExpenseEntity entity) {
        return new Expense(entity.getId(), entity.getAmount(), entity.getDescription(), entity.getDate(), entity.getInstallment());
    }
}
