package me.sigom.common.exceptions;


public class ExecutionTimeoutException extends RuntimeException {

    public ExecutionTimeoutException(String message) {
        super(message);
    }
}
