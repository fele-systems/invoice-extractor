package com.systems.fele.invoices.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.systems.fele.invoices.entity.ExpenseEntity;
import com.systems.fele.invoices.entity.Installment;
import com.systems.fele.invoices.entity.Installment.InstallmentDeserializer;
import com.systems.fele.invoices.entity.Installment.InstallmentSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a expense inside an invoice
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpenseRequest {
    private BigDecimal amount;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonSerialize(using = InstallmentSerializer.class)
    @JsonDeserialize(using = InstallmentDeserializer.class)
    private Installment installment;

    public Installment getInstallment() {
        return installment == null ? Installment.NULL : installment;
    }

    public static CreateExpenseRequest fromEntity(ExpenseEntity entity) {
        return new CreateExpenseRequest(entity.getAmount(), entity.getDescription(), entity.getDate(), entity.getInstallment());
    }
}