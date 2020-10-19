package io.bytetrend.geo.location.sink;

public class SinkException extends Exception {
    public SinkException() {
    }

    public SinkException(String message) {
        super(message);
    }

    public SinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
