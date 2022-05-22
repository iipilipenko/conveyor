package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@PropertySource("classpath:creditConveyor.properties")
public class ConveyorService {

    @Value("${base.rate}")
    double baseRate;

    BigDecimal minAmount = new BigDecimal(10000);

    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {

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
            return new ResponseEntity.status(HttpStatus.BAD_REQUEST.);
        }

    }

}
