package com.smb.invoiceservice.service;

import com.smb.invoiceservice.exception.InvoiceNotFoundException;
import com.smb.invoiceservice.exception.UserNotEligibleForInvoiceAmountException;
import com.smb.invoiceservice.model.Invoice;

public interface InvoiceService {
    Invoice getInvoiceById(Long id) throws InvoiceNotFoundException;

    Invoice createNewInvoice(Invoice invoice) throws UserNotEligibleForInvoiceAmountException;
}
