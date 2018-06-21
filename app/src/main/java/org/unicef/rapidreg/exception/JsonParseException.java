package org.unicef.rapidreg.exception;

public class JsonParseException extends RuntimeException {
    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(Throwable throwable) {
        super(throwable);
    }
}
