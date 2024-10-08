package com.systems.fele.invoices.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a expense inside an invoice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@ToString(exclude = {"invoiceId"})
public class ExpenseEntity {

    private long id;

    private long localId;

    private long invoiceId;

    private BigDecimal amount;

    private String description;

    private LocalDate date;

    @Embedded
    private Installment installment;

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null || !rhs.getClass().equals(ExpenseEntity.class)) return false;

        var typed = (ExpenseEntity) rhs;

        return Objects.equals(this.getId(), typed.getId()) &&
            this.getLocalId() == typed.getLocalId() &&
            Objects.compare(this.getAmount(), typed.getAmount(), BigDecimal::compareTo) == 0 &&
            Objects.equals(this.getDescription(), typed.getDescription()) &&
            Objects.equals(this.getDate(), typed.getDate()) &&
            Objects.equals(this.getInstallment(), typed.getInstallment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, localId, amount, description, date, installment);
    }
}
