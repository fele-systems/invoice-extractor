package com.systems.fele.extractor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Installment {
    int totalInstallments;
    int installmentNo;

    @Override
    public String toString() {
        return installmentNo + "/" + totalInstallments;
    }
}
