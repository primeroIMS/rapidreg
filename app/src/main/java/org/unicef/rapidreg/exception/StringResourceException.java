package org.unicef.rapidreg.exception;

public class StringResourceException extends Exception {
    public StringResourceException(String detailMessage) {
        super(detailMessage);
    }

    public StringResourceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
