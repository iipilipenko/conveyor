package com.pilipenko.creditconveyor.service;


import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@AllArgsConstructor
public class PreScoringAndLoanOfferConfiguration {

    @Autowired
    private ConveyorConfigurationParams conveyorConfigurationParams;

    LoanOfferDTO createLoanOfferDTO(boolean isInsuranceEnabled,
                                    boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO,
                                    Long applicationId) {

        log.info(applicationId + ": Pre scoring loan offer insurance: " + isInsuranceEnabled + " salary client: " + isSalaryClient);

        Double rate = conveyorConfigurationParams.getBaseRate();
        BigDecimal additionalServicesAmount;
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount();

        if (isSalaryClient) {
            rate += conveyorConfigurationParams.getInfluenceOfSalaryClientOnTheRate();
            log.info(applicationId + ": Rate decreased by 1 - salary client");
        }

        if (isInsuranceEnabled) {
            rate += conveyorConfigurationParams.getInfluenceOfInsuranceOnTheRate();
            log.info(applicationId + ": Rate decreased by 3 - insurance enabled");
            additionalServicesAmount = totalAmount.multiply(BigDecimal.valueOf(conveyorConfigurationParams.getMultiplierForCalculatingInsuranceCost()))
                    .setScale(2, RoundingMode.HALF_UP);
            log.info(applicationId + ": Insurance coast " + additionalServicesAmount);
            totalAmount = totalAmount.add(additionalServicesAmount).setScale(2, RoundingMode.HALF_UP);
        }
        // formula -> costOfUsingLoan = totalAmount * ( rate% / (12month * 100))
        BigDecimal costOfUsingALoan = totalAmount.multiply(
                new BigDecimal(rate / 1200 * loanApplicationRequestDTO.getTerm())).setScale(2, RoundingMode.HALF_UP);
        log.info(applicationId + ": Cost of using loan " + costOfUsingALoan);

        totalAmount = totalAmount.add(costOfUsingALoan).setScale(2, RoundingMode.HALF_UP);
        log.info(applicationId + ": Total loan amount " + totalAmount);

        BigDecimal monthlyPayment = totalAmount.divide(new BigDecimal(loanApplicationRequestDTO.getTerm())
                .setScale(2, RoundingMode.HALF_UP), 3, RoundingMode.HALF_UP);
        log.info(applicationId + ": Monthly payment " + monthlyPayment);

        log.info("Creating loan offers for user: id" + applicationId + " " +
                "name :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 1 loan offer is created with : rate " + rate +
                ", amount " + totalAmount.setScale(2, RoundingMode.HALF_UP) + ", term " + loanApplicationRequestDTO.getTerm() + ", monthly payment " +
                monthlyPayment.setScale(2, RoundingMode.HALF_UP));

        return new LoanOfferDTO(applicationId, loanApplicationRequestDTO.getAmount(),
                totalAmount, loanApplicationRequestDTO.getTerm(),
                monthlyPayment, new BigDecimal(rate),
                isInsuranceEnabled, isSalaryClient);
    }


}
