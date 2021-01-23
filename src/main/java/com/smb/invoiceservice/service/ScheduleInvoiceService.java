package com.smb.invoiceservice.service;

import com.smb.invoiceservice.exception.InvoiceCannotBeScheduledMultipleTimesException;
import com.smb.invoiceservice.exception.InvoiceNotFoundException;
import com.smb.invoiceservice.exception.ScheduledInvoiceNotFoundException;
import com.smb.invoiceservice.exception.UserNotEligibleForInvoiceAmountException;
import com.smb.invoiceservice.model.ScheduledInvoice;

import java.util.Date;

public interface ScheduleInvoiceService {
    ScheduledInvoice createNewScheduledInvoice(ScheduledInvoice scheduledInvoice) throws UserNotEligibleForInvoiceAmountException;

    ScheduledInvoice addScheduleToInvoice(Long id, Date futureDate) throws InvoiceNotFoundException, UserNotEligibleForInvoiceAmountException, InvoiceCannotBeScheduledMultipleTimesException;

    void cancelInvoiceSchedule(Long id) throws InvoiceNotFoundException, ScheduledInvoiceNotFoundException;

    boolean isInvoiceScheduled(Long invoiceId) throws ScheduledInvoiceNotFoundException;

    ScheduledInvoice getScheduledInvoice(Long id) throws ScheduledInvoiceNotFoundException;
}
