package com.pilipenko.deal.service.impl;

import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.repository.CreditRepository;
import com.pilipenko.deal.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private CreditRepository creditRepository;

    @Override
    public Credit createNewCredit(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Credit credit = new Credit()
                .setAmount(loanApplicationRequestDTO.getAmount())
                .setTerm(loanApplicationRequestDTO.getTerm());
        log.info(String.format("New credit saved: %s",credit));
        return creditRepository.save(credit);
    }

    @Override
    public Credit updateTotalCreditAmountWithAppliedOffer(Credit credit, LoanOfferDTO loanOfferDTO) {
        credit.setAmount(loanOfferDTO.getTotalAmount());
        log.info(String.format("Credit total amount updated with applied offer: amount %s",credit.getAmount()));
        return creditRepository.save(credit);
    }
}
