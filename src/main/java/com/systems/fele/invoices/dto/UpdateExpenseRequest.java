package com.systems.fele.invoices.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.Installment;
import com.systems.fele.invoices.entity.Installment.InstallmentRawDeserializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a expense inside an invoice
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpenseRequest {
    private BigDecimal amount;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonDeserialize(using = InstallmentRawDeserializer.class)
    private Installment installment;

    public static UpdateExpenseRequest fromEntity(ExpenseEntity entity) {
        return new UpdateExpenseRequest(entity.getAmount(), entity.getDescription(), entity.getDate(), entity.getInstallment());
    }
}