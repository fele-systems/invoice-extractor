package com.systems.fele.extractor.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ListInvoicesResponse {
    List<Invoice> invoices;
    
    @JsonProperty("expand_expenses")
    boolean expandExpenses;
}
