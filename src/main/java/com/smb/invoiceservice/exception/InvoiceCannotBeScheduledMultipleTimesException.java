package com.smb.invoiceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Invoice Cannot be Scheduled Multiple Times")
public class InvoiceCannotBeScheduledMultipleTimesException extends Exception {
    public InvoiceCannotBeScheduledMultipleTimesException(String message) {
        super(message);
    }
}
