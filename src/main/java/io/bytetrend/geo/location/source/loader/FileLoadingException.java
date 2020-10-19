package io.bytetrend.geo.location.source.loader;

public class FileLoadingException extends Exception {
    public FileLoadingException() {
    }

    public FileLoadingException(String message) {
        super(message);
    }

    public FileLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
