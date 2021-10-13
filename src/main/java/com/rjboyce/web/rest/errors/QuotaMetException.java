package com.rjboyce.web.rest.errors;

public class QuotaMetException extends RuntimeException {

    public QuotaMetException(String event) {
        super("Quota for event '" + event + "' has been met.");
    }
}
