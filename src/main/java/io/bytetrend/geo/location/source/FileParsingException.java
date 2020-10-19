package io.bytetrend.geo.location.source;

public class FileParsingException extends RuntimeException {
    public FileParsingException() {
    }

    public FileParsingException(String message) {
        super(message);
    }

    public FileParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
