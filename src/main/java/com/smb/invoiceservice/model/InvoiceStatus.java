package com.smb.invoiceservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class InvoiceStatus {
    private final Long invoiceId;
    private final BigDecimal amount;
    private final boolean isScheduled;
}
