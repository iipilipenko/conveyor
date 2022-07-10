package com.pilipenko.application.service;


import com.pilipenko.application.dto.LoanApplicationRequestDTO;
import com.pilipenko.application.dto.LoanOfferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class ApplicationService {

    @Autowired
    private RestTemplateService restTemplateService;

    public List<LoanOfferDTO> createLoanOffers(@Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {
        try {
            List<LoanOfferDTO> loanOffers = restTemplateService.postToConveyorOffers(loanApplicationRequestDTO);
            Comparator<LoanOfferDTO> rateComparator = Comparator.comparing(LoanOfferDTO::getRate);
            loanOffers.sort(Collections.reverseOrder(rateComparator));
            return loanOffers;
        } catch (URISyntaxException | NullPointerException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public void setAppliedOffer(LoanOfferDTO loanOfferDTO) {
        try {
            restTemplateService.postWithAppliedLoanOffer(loanOfferDTO);
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
    }


}
