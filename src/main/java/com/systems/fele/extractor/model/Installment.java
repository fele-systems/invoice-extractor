package com.systems.fele.extractor.model;

public class Installment {
    int totalInstallments;
    int installmentNo;
    public int getTotalInstallments() {
        return totalInstallments;
    }
    public int getInstallmentNo() {
        return installmentNo;
    }
    public Installment(int totalInstallments, int installmentNo) {
        this.totalInstallments = totalInstallments;
        this.installmentNo = installmentNo;
    }
    @Override
    public String toString() {
        return totalInstallments + "/" + installmentNo;
    }
}
