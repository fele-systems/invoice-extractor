package com.systems.fele.common.time;

import java.time.Month;

import com.systems.fele.extractor.exception.InvoiceFormatException;

public class Months {
    private Months() {}

    public static Month monthStrToInt(String monthStr) {
        return switch (monthStr.toLowerCase()) {
            case "jan" -> Month.JANUARY;
            case "fev" -> Month.FEBRUARY;
            case "mar" -> Month.MARCH;
            case "abr" -> Month.APRIL;
            case "mai" -> Month.MAY;
            case "jun" -> Month.JUNE;
            case "jul" -> Month.JULY;
            case "ago" -> Month.AUGUST;
            case "set" -> Month.SEPTEMBER;
            case "out" -> Month.OCTOBER;
            case "nov" -> Month.NOVEMBER;
            case "dec" -> Month.DECEMBER;
            default -> throw new InvoiceFormatException("Invalid expense date for: " + monthStr);
        };
    }
}
