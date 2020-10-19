package io.bytetrend.geo.location.model;

import java.io.Serializable;

/**
 * Response return when an error takes place.
 */
public class RestResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String errorText;
    private ErrorCode errorCode;

    public RestResponse(String errorText, ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorText = errorText;
    }

    public enum ErrorCode {
        SERVICE_LAYER_ERROR,
        INVALID_PARAMETER,
        EMPTY_REQUEST,
        UNKNOWN_ERROR

    }
}