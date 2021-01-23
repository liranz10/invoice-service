package com.smb.invoiceservice.service.impl;

import com.smb.invoiceservice.data.ScheduledInvoiceRepository;
import com.smb.invoiceservice.exception.InvoiceCannotBeScheduledMultipleTimesException;
import com.smb.invoiceservice.exception.InvoiceNotFoundException;
import com.smb.invoiceservice.exception.ScheduledInvoiceNotFoundException;
import com.smb.invoiceservice.exception.UserNotEligibleForInvoiceAmountException;
import com.smb.invoiceservice.model.Invoice;
import com.smb.invoiceservice.model.ScheduledInvoice;
import com.smb.invoiceservice.service.InvoiceService;
import com.smb.invoiceservice.service.ScheduleInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class ScheduleInvoiceServiceImpl implements ScheduleInvoiceService {

    private final InvoiceService invoiceService;
    private final ScheduledInvoiceRepository scheduledInvoiceRepository;

    @Autowired
    public ScheduleInvoiceServiceImpl(InvoiceService invoiceService, ScheduledInvoiceRepository scheduledInvoiceRepository) {
        this.invoiceService = invoiceService;
        this.scheduledInvoiceRepository = scheduledInvoiceRepository;
    }

    @Override
    public ScheduledInvoice createNewScheduledInvoice(ScheduledInvoice scheduledInvoice) throws UserNotEligibleForInvoiceAmountException {
        invoiceService.createNewInvoice(scheduledInvoice.getInvoice());
        return saveScheduledInvoice(scheduledInvoice);
    }

    @Override
    public ScheduledInvoice addScheduleToInvoice(Long id, Date futureDate) throws InvoiceNotFoundException, UserNotEligibleForInvoiceAmountException, InvoiceCannotBeScheduledMultipleTimesException {
        if (scheduledInvoiceRepository.findByInvoice_InvoiceId(id).isPresent()) {
            throw new InvoiceCannotBeScheduledMultipleTimesException("Invoice was Already Scheduled");
        } else {
            Invoice invoice = invoiceService.getInvoiceById(id);
            ScheduledInvoice scheduledInvoice = new ScheduledInvoice(invoice, futureDate);
            return saveScheduledInvoice(scheduledInvoice);
        }
    }

    @Override
    public void cancelInvoiceSchedule(Long id) throws InvoiceNotFoundException, ScheduledInvoiceNotFoundException {
        ScheduledInvoice scheduledInvoiceToCancel = getScheduledInvoice(id);
        scheduledInvoiceRepository.delete(scheduledInvoiceToCancel);
        log.info("Canceled Schedule for Invoice: " + scheduledInvoiceToCancel.getInvoice().getInvoiceId());

    }

    @Override
    public boolean isInvoiceScheduled(Long invoiceId) {
        try {
            return getScheduledInvoice(invoiceId) != null;
        } catch (ScheduledInvoiceNotFoundException e) {
            return false;
        }
    }

    public ScheduledInvoice getScheduledInvoice(Long id) throws ScheduledInvoiceNotFoundException {
        log.info("Searching a Schedule Invoice By Id");
        ScheduledInvoice scheduledInvoice = scheduledInvoiceRepository.findByInvoice_InvoiceId(id).
                orElseThrow(() -> new ScheduledInvoiceNotFoundException("Schedule not Found for Invoice"));
        return scheduledInvoice;
    }

    private ScheduledInvoice saveScheduledInvoice(ScheduledInvoice scheduledInvoice) {
        ScheduledInvoice createdScheduledInvoice = scheduledInvoiceRepository.save(scheduledInvoice);
        log.info("Successfully Created Schedule: " + scheduledInvoice.getScheduleId() +
                " for Invoice: " + scheduledInvoice.getInvoice().getInvoiceId());
        return createdScheduledInvoice;
    }

}
