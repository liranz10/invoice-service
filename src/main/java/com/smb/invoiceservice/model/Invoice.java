package com.smb.invoiceservice.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue
    private Long invoiceId;

    @NotNull(message = "amount is mandatory")
    private BigDecimal amount;

    @NotNull(message = "creationDate is mandatory")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date creationDate;

    private String description;

    @NotNull(message = "dueDate is mandatory")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dueDate;

    @NotNull(message = "companyName is mandatory")
    private String companyName;

    @Email(message = "email not well formed")
    @NotNull(message = "customerEmail is mandatory")
    private String customerEmail;
}
