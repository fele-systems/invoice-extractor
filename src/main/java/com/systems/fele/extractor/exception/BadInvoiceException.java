package com.systems.fele.extractor.exception;


/**
 * Thrown when something bad happens during extraction
 */
public class BadInvoiceException extends RuntimeException {
    public BadInvoiceException(String message) {
        super(message);
    }

    public BadInvoiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
