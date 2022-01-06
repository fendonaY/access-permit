package com.yyp.permit.exception;

public class PermitException extends RuntimeException {

    private String message;

    public PermitException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
