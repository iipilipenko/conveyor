package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.annotation.processing.Generated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@PropertySource("classpath:creditConveyor.properties")
public class ConveyorService {

    @Value("${base.rate}")
    Integer baseRate;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    BigDecimal minAmount = new BigDecimal(10000);

    public List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        if (loanApplicationRequestDTO.getFirstName().length() < 2
                || loanApplicationRequestDTO.getFirstName().length() > 30) {
            return null;
        }
        if (loanApplicationRequestDTO.getLastName().length() < 2
                || loanApplicationRequestDTO.getLastName().length() > 30) {
            return null;
        }
        if (loanApplicationRequestDTO.getMiddleName() != null) {
            if (loanApplicationRequestDTO.getMiddleName().length() > 30
                    || loanApplicationRequestDTO.getMiddleName().length() < 2) {
                return null;
            }
        }
        if (loanApplicationRequestDTO.getAmount().compareTo(minAmount) <= 0) {
            return null;
        }
        if (loanApplicationRequestDTO.getTerm() < 6) {
            return null;
        }
        if (Period.between(LocalDate.now(), loanApplicationRequestDTO.getBirthdate()).getYears() < 18) {
            return null;
        }
        if (!loanApplicationRequestDTO.getEmail().matches("[\\\\w\\\\.]{2,50}@[\\\\w\\\\.]{2,20}")) {
            return null;
        }
        if (!loanApplicationRequestDTO.getPassportSeries().matches("[\\d]{4}")) {
            return null;
        }
        if (!loanApplicationRequestDTO.getPassportNumber().matches("[\\d]{6}")) {
            return null;
        }

        ArrayList<LoanOfferDTO> loanOffers = new ArrayList<>();
        loanOffers.add(getLoanOfferDTO(true, true, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(false, false, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(true, false, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(false, true, loanApplicationRequestDTO));

        return loanOffers;


    }

    private LoanOfferDTO getLoanOfferDTO(boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Integer rate = baseRate;
        BigDecimal additionalServicesAmount = new BigDecimal(0);
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount();

        if (isSalaryClient) {
            rate -= 1;
        }

        if (isInsuranceEnabled) {
            rate -= 3;
            additionalServicesAmount = totalAmount.multiply(new BigDecimal(0.01));
        }

        BigDecimal costOfUsingALoan = loanApplicationRequestDTO.getAmount().multiply(
                new BigDecimal(rate * loanApplicationRequestDTO.getTerm()));

        totalAmount = totalAmount.add(additionalServicesAmount);
        totalAmount = totalAmount.add(costOfUsingALoan);
        BigDecimal monthlyPayment = totalAmount.divide(new BigDecimal(loanApplicationRequestDTO.getTerm()));

        return new LoanOfferDTO(applicationId, loanApplicationRequestDTO.getAmount(),
                totalAmount, loanApplicationRequestDTO.getTerm(),
                monthlyPayment, new BigDecimal(rate),
                isInsuranceEnabled, isSalaryClient);
    }


}
