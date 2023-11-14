package com.systems.fele.extractor.exception;

/**
 * When the invoice is given in the wrong format. It is not known if the invoice is valid or not.
 */
public class InvoiceFormatException extends RuntimeException {
    public InvoiceFormatException(String message) {
        super(message);
    }

    public InvoiceFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
