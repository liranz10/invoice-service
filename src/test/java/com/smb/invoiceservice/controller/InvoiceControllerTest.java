package com.smb.invoiceservice.controller;

import com.smb.invoiceservice.exception.InvoiceCannotBeScheduledMultipleTimesException;
import com.smb.invoiceservice.exception.InvoiceNotFoundException;
import com.smb.invoiceservice.exception.UserNotEligibleForInvoiceAmountException;
import com.smb.invoiceservice.model.Invoice;
import com.smb.invoiceservice.model.ScheduledInvoice;
import com.smb.invoiceservice.service.InvoiceService;
import com.smb.invoiceservice.service.ScheduleInvoiceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;


@ExtendWith(SpringExtension.class)
@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private InvoiceService invoiceService;
    @MockBean
    private ScheduleInvoiceService scheduleInvoiceService;


    @Test
    public void testGenerateInvoice() throws Exception {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.TEN, new Date(), "test"
                , new Date(), "company", "test@test.com");

        mvc.perform(post("/v1/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(invoice)))
                .andExpect(status().isOk());

        verify(invoiceService).createNewInvoice(Mockito.any(Invoice.class));
    }

    @Test
    public void testGetInvoiceById() throws Exception {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.TEN, new Date(), "test"
                , new Date(), "company", "test@test.com");
        Mockito.when(invoiceService.getInvoiceById(Long.valueOf(1))).thenReturn(invoice);
        mvc.perform(get("/v1/invoice/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId", is(1)));
    }

    @Test
    public void testCreateNewScheduledInvoice() throws Exception {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.TEN, new Date(), "test"
                , new Date(), "company", "test@test.com");
        ScheduledInvoice scheduledInvoice = new ScheduledInvoice(invoice, new Date());
        Mockito.when(invoiceService.getInvoiceById(Long.valueOf(1))).thenReturn(invoice);

        mvc.perform(post("/v1/scheduledInvoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(scheduledInvoice)))
                .andExpect(status().isOk());

        verify(scheduleInvoiceService).createNewScheduledInvoice(Mockito.any(ScheduledInvoice.class));
    }

    @Test
    public void testCancelInvoiceSchedule() throws Exception {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.TEN, new Date(), "test"
                , new Date(), "company", "test@test.com");
        ScheduledInvoice scheduledInvoice = new ScheduledInvoice(invoice, new Date());
        Mockito.when(scheduleInvoiceService.getScheduledInvoice(Long.valueOf(1))).thenReturn(scheduledInvoice);

        mvc.perform(delete("/v1/invoice/cancelSchedule/1"))
                .andExpect(status().isOk());

        verify(scheduleInvoiceService).cancelInvoiceSchedule(invoice.getInvoiceId());
    }

    @Test
    public void getInvoiceStatus() throws Exception {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.TEN, new Date(), "test"
                , new Date(), "company", "test@test.com");
        ScheduledInvoice scheduledInvoice = new ScheduledInvoice(invoice, new Date());
        Mockito.when(invoiceService.getInvoiceById(Long.valueOf(1))).thenReturn(invoice);
        Mockito.when(scheduleInvoiceService.getScheduledInvoice(Long.valueOf(1))).thenReturn(scheduledInvoice);
        Mockito.when(scheduleInvoiceService.isInvoiceScheduled(Long.valueOf(1))).thenReturn(true);

        mvc.perform(get("/v1/invoice/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId", is(1)))
                .andExpect(jsonPath("$.amount", is(10)))
                .andExpect(jsonPath("$.scheduled", is(true)));

    }
}