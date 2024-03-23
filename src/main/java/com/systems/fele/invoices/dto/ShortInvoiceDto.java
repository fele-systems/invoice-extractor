package com.systems.fele.invoices.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShortInvoiceDto {
    private long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private int numberOfExpenses;

    private BigDecimal total;
}
