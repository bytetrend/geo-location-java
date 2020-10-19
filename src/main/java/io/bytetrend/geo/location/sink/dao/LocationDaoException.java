package io.bytetrend.geo.location.sink.dao;

public class LocationDaoException extends Exception {
    public LocationDaoException() {
    }

    public LocationDaoException(String message) {
        super(message);
    }

    public LocationDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
