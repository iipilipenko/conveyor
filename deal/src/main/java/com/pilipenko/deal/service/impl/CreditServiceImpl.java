package com.pilipenko.deal.service.impl;

import com.pilipenko.deal.dto.CreditDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.enums.CreditStatus;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.model.PaymentSchedule;
import com.pilipenko.deal.repository.CreditRepository;
import com.pilipenko.deal.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Credit createNewCredit(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Credit credit = new Credit()
                .setAmount(loanApplicationRequestDTO.getAmount())
                .setTerm(loanApplicationRequestDTO.getTerm());
        log.info(String.format("New credit saved: %s", credit));
        return creditRepository.save(credit);
    }

    @Override
    public Credit updateCreditWithAppliedOffer(Credit credit, LoanOfferDTO loanOfferDTO) {
        credit.setAmount(loanOfferDTO.getTotalAmount())
                .setTerm(loanOfferDTO.getTerm())
                .setIsInsuranceEnabled(loanOfferDTO.getIsInsuranceEnabled())
                .setIsSalaryClient(loanOfferDTO.getIsSalaryClient())
                .setMonthlyPayment(loanOfferDTO.getMonthlyPayment());
        log.info(String.format("Credit total amount updated with applied offer: amount %s", credit.getAmount()));
        return creditRepository.save(credit);
    }

    @Override
    public Credit updateCreditWithCreditDTO(Credit credit, CreditDTO creditDTO) {
        credit.setMonthlyPayment(creditDTO.getMonthlyPayment())
                .setRate(creditDTO.getRate())
                .setPsk(creditDTO.getPsk())
                .setPaymentSchedule(creditDTO.getPaymentSchedule().stream()
                        .map(paymentScheduleElement -> modelMapper.map(paymentScheduleElement, PaymentSchedule.class))
                        .collect(Collectors.toList()))
                .setCreditStatus(CreditStatus.CALCULATED);
        log.info(String.format("Credit table updated with CreditDTO: %s", credit));
        return creditRepository.save(credit);
    }
}
