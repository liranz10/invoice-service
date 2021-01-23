package com.smb.invoiceservice.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "scheduled_invoices")
public class ScheduledInvoice {
    @Id
    @GeneratedValue
    private Long scheduleId;

    @OneToOne
    @NotNull(message = "invoice is mandatory")
    private Invoice invoice;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "paymentDate is mandatory")
    private Date paymentDate;

    public ScheduledInvoice(Invoice invoice, Date paymentDate) {
        this.invoice = invoice;
        this.paymentDate = paymentDate;
    }
}
