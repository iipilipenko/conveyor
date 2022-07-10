package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.enums.EmploymentStatus;
import com.pilipenko.creditconveyor.enums.Gender;
import com.pilipenko.creditconveyor.enums.JobPosition;
import com.pilipenko.creditconveyor.enums.MartialStatus;
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
class RateScoringTest {

    @Autowired
    private ConveyorConfigurationParams conveyorConfigurationParams;

    @Autowired
    private RateScoring rateScoring;

    @ParameterizedTest(name = "{index} loan denied")
    @ArgumentsSource(ClientParamsForScoringWithReasonForRefusingLoan.class)
    void testRateIsNull(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
                        MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
                        Gender gender, int currentWorkingExperience, int totalWorkingExperience,
                        boolean isInsuranceEnabled, boolean isSalaryClient) {
        Double rate = rateScoring.calculateRate(employmentStatus, jobPosition, salary, martialStatus,
                dependentAmount, birthDate, gender, currentWorkingExperience, totalWorkingExperience, isInsuranceEnabled,
                isSalaryClient, BigDecimal.valueOf(100000), 1L);
        Assert.isNull(rate, "method should return null");
    }

    @ParameterizedTest(name = "{index} rate is 17")
    @ArgumentsSource(ClientParamsForScoringRate17.class)
    void assertRateIs17(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
                        MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
                        Gender gender, int currentWorkingExperience, int totalWorkingExperience,
                        boolean isInsuranceEnabled, boolean isSalaryClient) {
        Double rate = rateScoring.calculateRate(employmentStatus, jobPosition, salary, martialStatus,
                dependentAmount, birthDate, gender, currentWorkingExperience, totalWorkingExperience, isInsuranceEnabled,
                isSalaryClient, BigDecimal.valueOf(100000), 2L);
        Assert.isTrue(Math.abs(rate-17)<0.0001, "rate should be 17");
    }

}