package com.systems.fele.invoices.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private List<CreateExpenseRequest> expenses;
}
