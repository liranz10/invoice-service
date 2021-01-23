package com.smb.invoiceservice.service.impl;

import com.smb.invoiceservice.data.InvoiceRepository;
import com.smb.invoiceservice.exception.InvoiceNotFoundException;
import com.smb.invoiceservice.exception.UserNotEligibleForInvoiceAmountException;
import com.smb.invoiceservice.model.Invoice;
import com.smb.invoiceservice.service.InvoiceService;
import com.smb.invoiceservice.service.RiskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Value("${riskScoreThreshold}")
    private static final int riskScoreThreshold = 90;
    @Value("${invoiceAmountThreshold}")
    private static final int invoiceAmountThreshold = 20000;

    private final InvoiceRepository invoiceRepository;
    private final RiskService riskService;


    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, RiskService riskService) {
        this.invoiceRepository = invoiceRepository;
        this.riskService = riskService;
    }

    @Override
    public Invoice getInvoiceById(Long id) throws InvoiceNotFoundException {
        log.info("Searching an Invoice By Id");
        return invoiceRepository.findById(id).
                orElseThrow(() -> new InvoiceNotFoundException("Invoice not Found"));
    }

    @Override
    public Invoice createNewInvoice(Invoice invoice) throws UserNotEligibleForInvoiceAmountException {
        if (isUserNotEligibleForInvoiceAmount(invoice)) {
            log.info("Invoice Amount " + invoice.getAmount() + " is not Eligible");
            throw new UserNotEligibleForInvoiceAmountException("Invoice Amount is not Eligible");
        } else {
            Invoice createdInvoice = invoiceRepository.save(invoice);
            log.info("Successfully Created Invoice:" + invoice.getInvoiceId());
            return createdInvoice;
        }
    }

    private boolean isUserNotEligibleForInvoiceAmount(Invoice invoice) {
        log.info("Checking Risk Score for Invoice:" + invoice.getInvoiceId());
        int riskScore = riskService.getUserRiskScore(invoice.getCustomerEmail());
        return isInvoiceNotEligible(riskScore, invoice.getAmount());
    }

    private boolean isInvoiceNotEligible(int riskScore, BigDecimal amount) {
        return riskScore <= riskScoreThreshold
                && (amount.compareTo(BigDecimal.valueOf(invoiceAmountThreshold)) > 0);
    }

}
