package com.systems.fele;

public class InvoiceFormatException extends RuntimeException {
    public InvoiceFormatException(String message) {
        super(message);
    }

    public InvoiceFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
