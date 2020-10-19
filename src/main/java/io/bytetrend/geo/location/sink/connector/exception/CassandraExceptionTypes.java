package io.bytetrend.geo.location.sink.connector.exception;

public interface CassandraExceptionTypes {
    String READ_FROM_DB_EXCEPTION = "ReadFromDBException";
    String WRITE_TO_DB_EXCEPTIION = "WriteToDBException";
    String TIMEOUT_EXCEPTION = "TimeoutException";
}
