package com.smb.invoiceservice.data;

import com.smb.invoiceservice.model.ScheduledInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduledInvoiceRepository extends JpaRepository<ScheduledInvoice, Long> {
    Optional<ScheduledInvoice> findByInvoice_InvoiceId(Long id);
}
