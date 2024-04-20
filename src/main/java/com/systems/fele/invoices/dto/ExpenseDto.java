package com.systems.fele.invoices.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.Installment;
import com.systems.fele.invoices.entity.Installment.InstallmentSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a expense inside an invoice
 */
@Getter
@AllArgsConstructor
public class ExpenseDto {
    private long id;
    
    private BigDecimal amount;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonSerialize(using = InstallmentSerializer.class)
    private Installment installment;

    public static ExpenseDto fromEntity(ExpenseEntity entity) {
        return new ExpenseDto(entity.getLocalId(), entity.getAmount(), entity.getDescription(), entity.getDate(), entity.getInstallment());
    }
}
