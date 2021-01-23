package com.smb.invoiceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Invoice Not Found")
public class UserNotEligibleForInvoiceAmountException extends Exception {
    public UserNotEligibleForInvoiceAmountException(String message) {
        super(message);
    }
}
