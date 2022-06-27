package com.pilipenko.deal.service;

import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.service.impl.CreditServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditService {
    Credit createNewCredit(LoanApplicationRequestDTO loanApplicationRequestDTO);
    Credit updateTotalCreditAmountWithAppliedOffer (Credit credit, LoanOfferDTO loanOfferDTO);
}
