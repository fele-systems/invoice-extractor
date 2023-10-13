package com.systems.fele.extractor.banks;

/**
 * Represents a invoice resource that will be loaded as LineStream to
 * be extracted later
 */
public interface InvoiceResource {

    /**
     * Loads this resource as line stream
     */
    LineStream loadAsLineStream();
}
