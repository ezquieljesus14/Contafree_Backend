package com.contafree.common.exception;

public class DuplicateResourceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String field;

    public DuplicateResourceException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
