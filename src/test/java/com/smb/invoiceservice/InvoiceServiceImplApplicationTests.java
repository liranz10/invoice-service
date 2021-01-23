package com.smb.invoiceservice;

import com.smb.invoiceservice.controller.InvoiceController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InvoiceServiceImplApplicationTests {

    @Autowired
    private InvoiceController invoiceController;

    @Test
    public void contextLoads() {
    }

    @Test
    public void smokeTest() {
        Assertions.assertNotNull(invoiceController);
    }

}
