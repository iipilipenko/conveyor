package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PreScoringAndConfigureLoanOfferTest {

    @Autowired
    private PreScoringAndLoanOfferConfiguration preScoring;

    @Test
    void assertPreScoringIsCorrect() {
        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000), 6, "Igor",
                "Pilipenko", null, "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
                "777", "777777");
        LoanOfferDTO loanOffer = preScoring.createLoanOfferDTO(true, true, request, 1L);
        Assert.notNull(loanOffer.getRate(), "rate should be not null");
        Assert.isTrue(loanOffer.getRate().compareTo(BigDecimal.valueOf(0)) > 0, "rate should be positive number");
        Assert.isTrue(loanOffer.getIsInsuranceEnabled(), "insurance should be true");
        Assert.isTrue(loanOffer.getIsSalaryClient(), "salary client should be true");
        Assert.isTrue(loanOffer.getMonthlyPayment().compareTo(BigDecimal.valueOf(0)) > 0, "monthly payment should be positive number");
        Assert.isTrue(loanOffer.getTotalAmount().compareTo(BigDecimal.valueOf(0)) > 0, "total amount should be positive number");
    }
}