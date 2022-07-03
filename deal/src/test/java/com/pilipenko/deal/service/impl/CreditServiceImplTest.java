package com.pilipenko.deal.service.impl;

import com.pilipenko.deal.dto.*;
import com.pilipenko.deal.enums.*;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.model.PaymentSchedule;
import com.pilipenko.deal.repository.CreditRepository;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreditServiceImplTest {

    private ModelMapper modelMapper = new ModelMapper();
    @Mock
    private CreditRepository creditRepository;
    @InjectMocks
    private CreditServiceImpl service = new CreditServiceImpl();

    @Test
    void createNewCredit() {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
                10, "Igor", "Pilipenko", "Igorevich",
                "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
                "4444", "666666");
        Credit creditReturnedByMock = new Credit()
                .setAmount(requestDTO.getAmount())
                .setTerm(requestDTO.getTerm());
        Mockito.when(creditRepository.save(ArgumentMatchers.any())).thenReturn(creditReturnedByMock);
        Credit creditReturnedByService = service.createNewCredit(requestDTO);
        assertEquals(creditReturnedByService, creditReturnedByMock);
    }

    @Test
    void updateCreditWithAppliedOffer() {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO(1000L, BigDecimal.valueOf(20000), BigDecimal.valueOf(30000),
                (Integer) 8, BigDecimal.valueOf(1000), BigDecimal.valueOf(20), true, true);
        Credit creditReturnedByMock = new Credit()
                .setAmount(loanOfferDTO.getTotalAmount())
                .setTerm(loanOfferDTO.getTerm())
                .setIsInsuranceEnabled(loanOfferDTO.getIsInsuranceEnabled())
                .setIsSalaryClient(loanOfferDTO.getIsSalaryClient())
                .setMonthlyPayment(loanOfferDTO.getMonthlyPayment());
        Mockito.when(creditRepository.save(ArgumentMatchers.any())).thenReturn(creditReturnedByMock);
        Credit creditReturnedByServiceMethod = service.updateCreditWithAppliedOffer(new Credit(), loanOfferDTO);
        assertEquals(creditReturnedByServiceMethod, creditReturnedByMock);
    }

    @Test
    void updateCreditWithCreditDTO() {
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();
        CreditDTO creditDTO = new CreditDTO()
                .setAmount(BigDecimal.valueOf(10000))
                .setTerm(10)
                .setMonthlyPayment(BigDecimal.valueOf(10000))
                .setRate(BigDecimal.valueOf(10000))
                .setPsk(BigDecimal.valueOf(10000))
                .setIsInsuranceEnabled(true)
                .setIsSalaryClient(true)
                .setPaymentSchedule(paymentSchedule);
        Credit creditReturnedByMock = new Credit()
                .setMonthlyPayment(creditDTO.getMonthlyPayment())
                .setRate(creditDTO.getRate())
                .setPsk(creditDTO.getPsk())
                .setPaymentSchedule(creditDTO.getPaymentSchedule().stream()
                        .map(paymentScheduleElement -> modelMapper.map(paymentScheduleElement, PaymentSchedule.class))
                        .collect(Collectors.toList()))
                .setCreditStatus(CreditStatus.CALCULATED);
        Mockito.when(creditRepository.save(ArgumentMatchers.any())).thenReturn(creditReturnedByMock);
        Credit creditReturnedByServiceMethod = service.updateCreditWithCreditDTO(new Credit(), creditDTO);
        assertEquals(creditReturnedByServiceMethod, creditReturnedByMock);

    }
}