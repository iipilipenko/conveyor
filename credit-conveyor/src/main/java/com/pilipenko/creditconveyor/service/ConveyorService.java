package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.CreditDTO;
import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import com.pilipenko.creditconveyor.dto.ScoringDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.util.*;

@Slf4j
@Service

public class ConveyorService {

    @Autowired
    public ConveyorConfigurationMethods conveyorConfigurationMethods;

    public List<LoanOfferDTO> createLoanOffers(@Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Long applicationId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        ArrayList<LoanOfferDTO> loanOffers = new ArrayList<>();
        loanOffers.add(conveyorConfigurationMethods.createLoanOfferDTO(true, true, loanApplicationRequestDTO, applicationId));
        loanOffers.add(conveyorConfigurationMethods.createLoanOfferDTO(false, false, loanApplicationRequestDTO, applicationId));
        loanOffers.add(conveyorConfigurationMethods.createLoanOfferDTO(true, false, loanApplicationRequestDTO, applicationId));
        loanOffers.add(conveyorConfigurationMethods.createLoanOfferDTO(false, true, loanApplicationRequestDTO, applicationId));

        Comparator<LoanOfferDTO> rateComparator = Comparator.comparing(LoanOfferDTO::getRate);
        loanOffers.sort(Collections.reverseOrder(rateComparator));
        return loanOffers;
    }

    public CreditDTO createCreditDTO(@Valid ScoringDataDTO scoringDataDTO) {

        Long requestId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        Double rate = conveyorConfigurationMethods.calculateRate(scoringDataDTO.getEmployment().getEmploymentStatus(), scoringDataDTO.getEmployment().getPosition(),
                scoringDataDTO.getEmployment().getSalary(), scoringDataDTO.getMaritalStatus(), scoringDataDTO.getDependentAmount(),
                scoringDataDTO.getBirthdate(), scoringDataDTO.getGender(), scoringDataDTO.getEmployment().getWorkExperienceCurrent(),
                scoringDataDTO.getEmployment().getWorkExperienceTotal(), scoringDataDTO.getIsInsuranceEnabled(),
                scoringDataDTO.getIsSalaryClient(), scoringDataDTO.getAmount(), requestId);

        if (rate == null) {
            return null;
        }

        return conveyorConfigurationMethods.calculateCreditParams(rate, scoringDataDTO.getAmount(), scoringDataDTO.getTerm(),
                scoringDataDTO.getIsInsuranceEnabled(), scoringDataDTO.getIsSalaryClient(), requestId);

    }
}
