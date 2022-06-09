package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.CreditDTO;
import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import com.pilipenko.creditconveyor.dto.ScoringDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service

public class ConveyorService {

    @Autowired
    public ConveyorConfiguration conveyorConfiguration;

    public List<LoanOfferDTO> createLoanOffers(@Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Long applicationId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        ArrayList<LoanOfferDTO> loanOffers = new ArrayList<>();
        loanOffers.add(conveyorConfiguration.getLoanOfferDTO(true, true, loanApplicationRequestDTO, applicationId));
        loanOffers.add(conveyorConfiguration.getLoanOfferDTO(false, false, loanApplicationRequestDTO, applicationId));
        loanOffers.add(conveyorConfiguration.getLoanOfferDTO(true, false, loanApplicationRequestDTO, applicationId));
        loanOffers.add(conveyorConfiguration.getLoanOfferDTO(false, true, loanApplicationRequestDTO, applicationId));

        log.info("Creating loan offers for user: " + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() +
                "user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 1 loan offer is created with : rate " + loanOffers.get(0).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(0).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(0).getTerm() + ", monthly payment " +
                loanOffers.get(0).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP) + "\n" +
                "user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 2 loan offer is created with : rate " + loanOffers.get(1).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(1).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(1).getTerm() + ", monthly payment " +
                loanOffers.get(1).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP) + "\n" +
                "user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 3 loan offer is created with : rate " + loanOffers.get(2).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(2).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(2).getTerm() + ", monthly payment " +
                loanOffers.get(2).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP) + "\n" +
                "user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 4 loan offer is created with : rate " + loanOffers.get(3).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(3).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(3).getTerm() + ", monthly payment " +
                loanOffers.get(3).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));

        Comparator<LoanOfferDTO> rateComparator = Comparator.comparing(LoanOfferDTO::getRate);
        loanOffers.sort(Collections.reverseOrder(rateComparator));
        return loanOffers;
    }



    public CreditDTO createCreditDTO(@Valid ScoringDataDTO scoringDataDTO) {

        log.info("Scoring loan offer for user: " + scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName());

        Double rate = conveyorConfiguration.calculateRate(scoringDataDTO.getEmployment().getEmploymentStatus(), scoringDataDTO.getEmployment().getPosition(),
                scoringDataDTO.getEmployment().getSalary(), scoringDataDTO.getMaritalStatus(), scoringDataDTO.getDependentAmount(),
                scoringDataDTO.getBirthdate(), scoringDataDTO.getGender(), scoringDataDTO.getEmployment().getWorkExperienceCurrent(),
                scoringDataDTO.getEmployment().getWorkExperienceTotal(), scoringDataDTO.getIsInsuranceEnabled(),
                scoringDataDTO.getIsSalaryClient(), scoringDataDTO.getAmount());

        if (rate == null) {
            return null;
        }

        log.info("Calculating credit params");

        return conveyorConfiguration.calculateCreditParams(rate, scoringDataDTO.getAmount(), scoringDataDTO.getTerm(),
                scoringDataDTO.getIsInsuranceEnabled(), scoringDataDTO.getIsSalaryClient());

    }






}
