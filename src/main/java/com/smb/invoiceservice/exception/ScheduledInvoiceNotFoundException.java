package com.smb.invoiceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Schedule Not Found")
public class ScheduledInvoiceNotFoundException extends Exception {
    public ScheduledInvoiceNotFoundException(String message) {
        super(message);
    }
}
