package com.pilipenko.deal.service;

import com.pilipenko.deal.dto.CreditDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.dto.ScoringDataDTO;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;

import java.net.URISyntaxException;
import java.util.List;

public interface RestTemplateService {
    List<LoanOfferDTO> postToConveyorOffers (LoanApplicationRequestDTO loanApplicationRequestDTO) throws URISyntaxException;
    CreditDTO postToConveyorCalculation (ScoringDataDTO scoringDataDTO) throws URISyntaxException;
}
