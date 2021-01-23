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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ScheduleInvoiceServiceImplTest {

    private ScheduleInvoiceService scheduleInvoiceService;
    @MockBean
    private InvoiceService invoiceService;
    @MockBean
    private ScheduledInvoiceRepository scheduledInvoiceRepository;

    @BeforeEach
    public void setUp() {
        scheduleInvoiceService = new ScheduleInvoiceServiceImpl(invoiceService, scheduledInvoiceRepository);
    }

    @Test
    void testCreateNewScheduledInvoice() throws UserNotEligibleForInvoiceAmountException, InvoiceNotFoundException {
        Invoice invoice = addMockedInvoice(100);
        ScheduledInvoice scheduledInvoice = new ScheduledInvoice(invoice, new Date());
        scheduleInvoiceService.createNewScheduledInvoice(scheduledInvoice);

        Mockito.verify(scheduledInvoiceRepository).save(scheduledInvoice);
    }

    @Test
    void testAddScheduleToInvoice() throws UserNotEligibleForInvoiceAmountException, InvoiceNotFoundException, InvoiceCannotBeScheduledMultipleTimesException {
        Invoice invoice = addMockedInvoice(100);
        scheduleInvoiceService.addScheduleToInvoice(invoice.getInvoiceId(), new Date());

        Mockito.verify(invoiceService).getInvoiceById(invoice.getInvoiceId());
        Mockito.verify(scheduledInvoiceRepository).findByInvoice_InvoiceId(invoice.getInvoiceId());
        Mockito.verify(scheduledInvoiceRepository,
                Mockito.times(1)).save(Mockito.any(ScheduledInvoice.class));
    }

    @Test
    void testCannotAddScheduleToInvoiceTwice() throws UserNotEligibleForInvoiceAmountException, InvoiceNotFoundException, InvoiceCannotBeScheduledMultipleTimesException {
        Invoice invoice = addMockedInvoice(100);
        scheduleInvoiceService.addScheduleToInvoice(invoice.getInvoiceId(), new Date());
        Mockito.when(scheduledInvoiceRepository.findByInvoice_InvoiceId(Long.valueOf(1))).
                thenReturn(Optional.of(new ScheduledInvoice(invoice, new Date())));

        assertThrows(InvoiceCannotBeScheduledMultipleTimesException.class, () -> {
            scheduleInvoiceService.addScheduleToInvoice(invoice.getInvoiceId(), new Date());
        });
    }

    @Test
    void testCancelInvoiceSchedule() throws InvoiceNotFoundException, ScheduledInvoiceNotFoundException {
        Invoice invoice = addMockedInvoice(100);
        Mockito.when(scheduledInvoiceRepository.findByInvoice_InvoiceId(Long.valueOf(1))).
                thenReturn(Optional.of(new ScheduledInvoice(invoice, new Date())));
        scheduleInvoiceService.cancelInvoiceSchedule(invoice.getInvoiceId());

        Mockito.verify(scheduledInvoiceRepository).delete(Mockito.any(ScheduledInvoice.class));
    }

    @Test
    void testIslInvoiceScheduled() throws InvoiceNotFoundException, ScheduledInvoiceNotFoundException {
        Invoice invoice = addMockedInvoice(100);
        Mockito.when(scheduledInvoiceRepository.findByInvoice_InvoiceId(Long.valueOf(1))).
                thenReturn(Optional.of(new ScheduledInvoice(invoice, new Date())));
        assertTrue(scheduleInvoiceService.isInvoiceScheduled(invoice.getInvoiceId()));
        assertFalse(scheduleInvoiceService.isInvoiceScheduled(Long.valueOf(2)));
    }

    private Invoice addMockedInvoice(int riskScore) throws InvoiceNotFoundException {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.TEN, new Date(), "test"
                , new Date(), "company", "test@test.com");
        Mockito.when(invoiceService.getInvoiceById(Long.valueOf(1)))
                .thenReturn(invoice);
        return invoice;
    }
}