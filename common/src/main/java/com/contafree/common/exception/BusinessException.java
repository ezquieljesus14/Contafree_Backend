package com.contafree.common.exception;

public class BusinessException extends RuntimeException {
    private final String errorCode;

    private static final long serialVersionUID = 1L;
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
