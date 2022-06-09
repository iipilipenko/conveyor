//package com.pilipenko.creditconveyor.service;
//
//import com.pilipenko.creditconveyor.dto.*;
//import com.pilipenko.creditconveyor.enums.EmploymentStatus;
//import com.pilipenko.creditconveyor.enums.Gender;
//import com.pilipenko.creditconveyor.enums.JobPosition;
//import com.pilipenko.creditconveyor.enums.MartialStatus;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ArgumentsSource;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.Assert;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//class ConveyorServiceTest {
//
//    @Autowired
//    private ConveyorService conveyorService;
//
//    @Test
//    void createLoanOffers() {
//
//        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
//                10, "Igor", "Pilipenko", null,
//                "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
//                "4444", "666666");
//
//        List<LoanOfferDTO> loanOfferDTOList = conveyorService.createLoanOffers(requestDTO);
//
//        Assert.notNull(loanOfferDTOList, "loan offers list must not be null");
//        Assert.isTrue(loanOfferDTOList.size() == 4, "list should have 4 loan offers");
//        for (int i = 1; i < loanOfferDTOList.size(); i++) {
//            Assert.isTrue(loanOfferDTOList.get(i).getRate().compareTo(loanOfferDTOList.get(i - 1).getRate()) < 0,
//                    "loan offers should be placed descending order");
//        }
//    }
//
//    @Test
//    void createCreditDTO() {
//        EmploymentDTO employmentDTO = new EmploymentDTO(EmploymentStatus.WORKING, "123456789012",
//                BigDecimal.valueOf(100000), JobPosition.JUNIOR, 20, 20);
//        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(100000), 6, "Igor",
//                "Pilipenko", "Igorevich", Gender.MALE, LocalDate.of(1993, 4, 21),
//                "3333", "333333", LocalDate.of(2030, 4, 21),
//                "test", MartialStatus.MARRIED, 0, employmentDTO, "test",
//                true, true);
//        CreditDTO creditDTO = conveyorService.createCreditDTO(scoringDataDTO);
//        Assert.isTrue(creditDTO != null, "should not be null");
//    }
//
//
//    @ParameterizedTest(name = "{index} loan denied")
//    @ArgumentsSource(ClientParamsForScoringWithReasonForRefusingLoan.class)
//    void testRateIsNull(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
//                       MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
//                       Gender gender, int currentWorkingExperience, int totalWorkingExperience,
//                       boolean isInsuranceEnabled, boolean isSalaryClient) {
//        Double rate = conveyorService.calculateRate(employmentStatus, jobPosition, salary, martialStatus,
//                dependentAmount, birthDate, gender, currentWorkingExperience, totalWorkingExperience, isInsuranceEnabled,
//                isSalaryClient, BigDecimal.valueOf(100000));
//        Assert.isNull(rate, "method should return null");
//    }
//
//    @ParameterizedTest(name = "{index} loan denied")
//    @ArgumentsSource(ClientParamsForScoringRate17.class)
//    void testRateIs(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
//                       MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
//                       Gender gender, int currentWorkingExperience, int totalWorkingExperience,
//                       boolean isInsuranceEnabled, boolean isSalaryClient) {
//        Double rate = conveyorService.calculateRate(employmentStatus, jobPosition, salary, martialStatus,
//                dependentAmount, birthDate, gender, currentWorkingExperience, totalWorkingExperience, isInsuranceEnabled,
//                isSalaryClient, BigDecimal.valueOf(100000));
//        Assert.isTrue(Math.abs(rate-17)<0.0001, "rate should be 17");
//    }
//
//}