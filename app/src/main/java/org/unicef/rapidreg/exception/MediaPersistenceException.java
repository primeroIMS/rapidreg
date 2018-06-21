package org.unicef.rapidreg.exception;

public class MediaPersistenceException extends Exception {
    public MediaPersistenceException() {
        super("Could not save media");
    }

    public MediaPersistenceException(String message) {
        super(message);
    }

    public MediaPersistenceException(Throwable throwable) {
        super(throwable);
    }
}
