package com.wirerest.shell;

public class ProcessStartException extends RuntimeException {
    public ProcessStartException(String command, Throwable cause) {
        super("ProcessStartException: Error starting process: " + command, cause);
    }
}
