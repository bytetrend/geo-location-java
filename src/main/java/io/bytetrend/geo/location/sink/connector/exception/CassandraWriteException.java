package io.bytetrend.geo.location.sink.connector.exception;

public class CassandraWriteException extends Exception {
    public CassandraWriteException() {
    }

    public CassandraWriteException(String message) {
        super(message);
    }

    public CassandraWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
