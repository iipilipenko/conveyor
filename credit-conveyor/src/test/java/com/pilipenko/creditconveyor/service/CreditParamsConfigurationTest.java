package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.CreditDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
@SpringBootTest

class CreditParamsConfigurationTest {

    @Autowired
    private CreditParamsConfiguration creditParamsConfiguration;

    @Test
    void assertCreditParamsIsCorrect() {
        CreditDTO creditDTO = creditParamsConfiguration.calculateCreditParams(11.5, BigDecimal.valueOf(100000),
                6, true, true, 001L);
        Assert.isTrue(creditDTO.getAmount().compareTo(BigDecimal.valueOf(100000))==0, "Amount should be 100 000");
        Assert.isTrue(creditDTO.getMonthlyPayment().setScale(0, BigDecimal.ROUND_FLOOR).compareTo(BigDecimal.valueOf(17230))==0, "Monthly payment should be ");
        Assert.isTrue(creditDTO.getPsk().setScale(2, BigDecimal.ROUND_FLOOR).compareTo(BigDecimal.valueOf(11.45))==0, "Rate PSK should be 11.45");
        Assert.isTrue(creditDTO.getPaymentSchedule().get(5).getRemainingDebt().compareTo(BigDecimal.valueOf(100))<0, "Remaining debt at the last payment should be less than 100ue");
    }

}