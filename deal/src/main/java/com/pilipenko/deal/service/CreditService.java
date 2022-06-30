package com.pilipenko.deal.service;

import com.pilipenko.deal.dto.CreditDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.service.impl.CreditServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditService {
    Credit createNewCredit(LoanApplicationRequestDTO loanApplicationRequestDTO);
    Credit updateCreditWithAppliedOffer(Credit credit, LoanOfferDTO loanOfferDTO);
    Credit updateCreditWithCreditDTO (Credit credit, CreditDTO creditDTO);
}
