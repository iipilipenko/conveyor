package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.CreditDTO;
import com.pilipenko.creditconveyor.enums.EmploymentStatus;
import com.pilipenko.creditconveyor.enums.Gender;
import com.pilipenko.creditconveyor.enums.JobPosition;
import com.pilipenko.creditconveyor.enums.MartialStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ConveyorConfigurationMethodsTest {

    @Autowired
    private ConveyorConfigurationParams conveyorConfigurationParams;

    @Autowired
    private ConveyorConfigurationMethods conveyorConfigurationMethods;

    @Test
    void calculateRate() {
    }

    @Test
    void calculateCreditParams() {
    }

    @ParameterizedTest(name = "{index} loan denied")
    @ArgumentsSource(ClientParamsForScoringWithReasonForRefusingLoan.class)
    void testRateIsNull(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
                        MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
                        Gender gender, int currentWorkingExperience, int totalWorkingExperience,
                        boolean isInsuranceEnabled, boolean isSalaryClient) {
        Double rate = conveyorConfigurationMethods.calculateRate(employmentStatus, jobPosition, salary, martialStatus,
                dependentAmount, birthDate, gender, currentWorkingExperience, totalWorkingExperience, isInsuranceEnabled,
                isSalaryClient, BigDecimal.valueOf(100000), 1L);
        Assert.isNull(rate, "method should return null");
    }

    @ParameterizedTest(name = "{index} loan denied")
    @ArgumentsSource(ClientParamsForScoringRate17.class)
    void assertRateIs17(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
                    MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
                    Gender gender, int currentWorkingExperience, int totalWorkingExperience,
                    boolean isInsuranceEnabled, boolean isSalaryClient) {
        Double rate = conveyorConfigurationMethods.calculateRate(employmentStatus, jobPosition, salary, martialStatus,
                dependentAmount, birthDate, gender, currentWorkingExperience, totalWorkingExperience, isInsuranceEnabled,
                isSalaryClient, BigDecimal.valueOf(100000), 2L);
        Assert.isTrue(Math.abs(rate-17)<0.0001, "rate should be 17");
    }

    @Test
    void assertCreditParamsIsCorrect() {
        CreditDTO creditDTO = conveyorConfigurationMethods.calculateCreditParams(11.5, BigDecimal.valueOf(100000),
                6, true, true, 001L);
        Assert.isTrue(creditDTO.getAmount().compareTo(BigDecimal.valueOf(100000))==0, "Amount should be 100 000");
        Assert.isTrue(creditDTO.getMonthlyPayment().setScale(0, BigDecimal.ROUND_FLOOR).compareTo(BigDecimal.valueOf(17230))==0, "Monthly payment should be ");
        Assert.isTrue(creditDTO.getPsk().setScale(2, BigDecimal.ROUND_FLOOR).compareTo(BigDecimal.valueOf(11.45))==0, "Rate PSK should be 11.45");
        Assert.isTrue(creditDTO.getPaymentSchedule().get(5).getRemainingDebt().compareTo(BigDecimal.valueOf(100))<0, "Remaining debt at the last payment should be less than 100ue");
    }
}