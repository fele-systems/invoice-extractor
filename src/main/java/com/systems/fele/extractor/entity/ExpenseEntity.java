package com.systems.fele.extractor.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.systems.fele.extractor.model.Installment;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a expense inside an invoice
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ExpenseEntity {

    public ExpenseEntity(InvoiceEntity tempInvoice, BigDecimal amount, String description, LocalDate date, Installment installment) {
        this(null, tempInvoice, amount, description, date, installment);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="expense_id")
    private InvoiceEntity invoice;

    private BigDecimal amount;

    private String description;

    private LocalDate date;

    @Embedded
    private Installment installment;
}
