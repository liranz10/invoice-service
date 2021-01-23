package com.smb.invoiceservice.service.impl;

import com.smb.invoiceservice.data.InvoiceRepository;
import com.smb.invoiceservice.exception.InvoiceNotFoundException;
import com.smb.invoiceservice.exception.UserNotEligibleForInvoiceAmountException;
import com.smb.invoiceservice.model.Invoice;
import com.smb.invoiceservice.service.InvoiceService;
import com.smb.invoiceservice.service.RiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class InvoiceServiceImplTest {

    private InvoiceService invoiceService;
    @MockBean
    private InvoiceRepository invoiceRepository;
    @MockBean
    private RiskService riskService;

    @BeforeEach
    public void setUp() {
        invoiceService = new InvoiceServiceImpl(invoiceRepository, riskService);
    }

    @Test
    public void testGetInvoiceByIdIsPresent() throws InvoiceNotFoundException {
        Invoice invoice = addMockedInvoice(100);

        assertEquals(invoice, invoiceService.getInvoiceById(Long.valueOf(1)));
        assertEquals(invoice.getInvoiceId(), Long.valueOf(1));
    }

    @Test
    public void testGetInvoiceByIdIsNotPresent() {
        Invoice invoice = addMockedInvoice(100);

        assertThrows(InvoiceNotFoundException.class, () -> {
            invoiceService.getInvoiceById(Long.valueOf(2));
        });
    }

    @Test
    void testCreateNewInvoice() throws UserNotEligibleForInvoiceAmountException {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.TEN, new Date(), "test"
                , new Date(), "company", "test@test.com");
        Mockito.when(riskService.getUserRiskScore(invoice.getCustomerEmail())).
                thenReturn(100);
        invoiceService.createNewInvoice(invoice);

        Mockito.verify(invoiceRepository).save(invoice);
    }

    @Test
    void testCreateNewInvoiceNotEligible() {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.valueOf(22000), new Date(), "test"
                , new Date(), "company", "test@test.com");
        Mockito.when(riskService.getUserRiskScore(invoice.getCustomerEmail())).
                thenReturn(80);

        assertThrows(UserNotEligibleForInvoiceAmountException.class, () -> {
            invoiceService.createNewInvoice(invoice);
        });
    }

    private Invoice addMockedInvoice(int riskScore) {
        Invoice invoice = new Invoice(Long.valueOf(1), BigDecimal.TEN, new Date(), "test"
                , new Date(), "company", "test@test.com");
        Mockito.when(invoiceRepository.findById(Long.valueOf(1)))
                .thenReturn(Optional.of(invoice));
        Mockito.when(riskService.getUserRiskScore(invoice.getCustomerEmail())).
                thenReturn(riskScore);
        return invoice;
    }
}