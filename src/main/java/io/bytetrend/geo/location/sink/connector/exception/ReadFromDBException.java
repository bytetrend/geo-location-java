package io.bytetrend.geo.location.sink.connector.exception;

public class ReadFromDBException extends Exception {
    public ReadFromDBException() {
    }

    public ReadFromDBException(String message) {
        super(message);
    }

    public ReadFromDBException(String message, Throwable cause) {
        super(message, cause);
    }
}
