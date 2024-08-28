package com.ericsson.eniq.events.ui.server.config;

/**
 * Exception thrown when application initialisation 
 * fails.
 * 
 * @author edeccox
 * @since 2010
 *
 */
public class ApplicationConfigInitException extends RuntimeException {
    public ApplicationConfigInitException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
