package com.smb.invoiceservice.service.impl;

import com.smb.invoiceservice.service.RiskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class RiskServiceImpl implements RiskService {

    @Override
    public int getUserRiskScore(String customerEmail) {
        log.info("Checking Risk Score for :" + customerEmail);
        return new Random().nextInt(100);
    }
}
