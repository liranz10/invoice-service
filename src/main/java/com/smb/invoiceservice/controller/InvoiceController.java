package com.smb.invoiceservice.controller;


import com.smb.invoiceservice.exception.InvoiceCannotBeScheduledMultipleTimesException;
import com.smb.invoiceservice.exception.InvoiceNotFoundException;
import com.smb.invoiceservice.exception.ScheduledInvoiceNotFoundException;
import com.smb.invoiceservice.exception.UserNotEligibleForInvoiceAmountException;
import com.smb.invoiceservice.model.Invoice;
import com.smb.invoiceservice.model.InvoiceStatus;
import com.smb.invoiceservice.model.ScheduledInvoice;
import com.smb.invoiceservice.service.InvoiceService;
import com.smb.invoiceservice.service.ScheduleInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/v1")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final ScheduleInvoiceService scheduleInvoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, ScheduleInvoiceService scheduleInvoiceService) {
        this.invoiceService = invoiceService;
        this.scheduleInvoiceService = scheduleInvoiceService;
    }

    @PostMapping("/invoice")
    public ResponseEntity<Invoice> generateInvoice(@Valid @RequestBody Invoice invoice)
            throws UserNotEligibleForInvoiceAmountException {
        log.info("New Invoice Creation Request");
        return new ResponseEntity<>(invoiceService.createNewInvoice(invoice), HttpStatus.OK);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<Invoice> getInvoiceById(@Valid @NotNull @PathVariable Long invoiceId) throws InvoiceNotFoundException {
        log.info("Get Invoice By Id Request");
        return new ResponseEntity<>(invoiceService.getInvoiceById(invoiceId), HttpStatus.OK);
    }

    @PostMapping("/scheduledInvoice")
    public ResponseEntity<ScheduledInvoice> createNewScheduledInvoice(@Valid @RequestBody ScheduledInvoice scheduledInvoice)
            throws UserNotEligibleForInvoiceAmountException {
        log.info("New Scheduled Invoice Creation Request");
        return new ResponseEntity<>(scheduleInvoiceService.createNewScheduledInvoice(scheduledInvoice), HttpStatus.OK);
    }

    @PostMapping("/invoice/addSchedule")
    public ResponseEntity<ScheduledInvoice> addScheduleToInvoice(@Valid @NotNull @RequestParam Long invoiceId,
                                                                 @Valid @NotNull @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date futureDate)
            throws UserNotEligibleForInvoiceAmountException, InvoiceNotFoundException, InvoiceCannotBeScheduledMultipleTimesException {
        log.info("New Add Schedule to Invoice Creation Request");
        return new ResponseEntity<>(scheduleInvoiceService.addScheduleToInvoice(invoiceId, futureDate), HttpStatus.OK);
    }

    @DeleteMapping("/invoice/cancelSchedule/{invoiceId}")
    public void cancelInvoiceSchedule(@Valid @NotNull @PathVariable Long invoiceId)
            throws InvoiceNotFoundException, ScheduledInvoiceNotFoundException {
        log.info("New Cancel Schedule to Invoice Creation Request");
        scheduleInvoiceService.cancelInvoiceSchedule(invoiceId);
    }

    @GetMapping("/invoice/status/{invoiceId}")
    public ResponseEntity<InvoiceStatus> getInvoiceStatus(@Valid @NotNull @PathVariable Long invoiceId) throws InvoiceNotFoundException, ScheduledInvoiceNotFoundException {
        log.info("Get Invoice Status Request");
        boolean isScheduled = scheduleInvoiceService.isInvoiceScheduled(invoiceId);
        BigDecimal amount = invoiceService.getInvoiceById(invoiceId).getAmount();

        return new ResponseEntity<>(new InvoiceStatus(invoiceId, amount, isScheduled), HttpStatus.OK);
    }

}
