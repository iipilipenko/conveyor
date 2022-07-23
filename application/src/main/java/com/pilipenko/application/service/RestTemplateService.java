package com.pilipenko.application.service;

import com.pilipenko.application.dto.LoanApplicationRequestDTO;
import com.pilipenko.application.dto.LoanOfferDTO;
import org.springframework.http.HttpStatus;

import java.net.URISyntaxException;
import java.util.List;

public interface RestTemplateService {
    List<LoanOfferDTO> postToConveyorOffers (LoanApplicationRequestDTO loanApplicationRequestDTO) throws URISyntaxException;
    void postWithAppliedLoanOffer (LoanOfferDTO loanOfferDTO) throws URISyntaxException;
}
