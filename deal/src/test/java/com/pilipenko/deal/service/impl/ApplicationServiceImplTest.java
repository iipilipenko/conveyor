package com.pilipenko.deal.service.impl;


import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationServiceImpl service;

    @Test
    void createNew() {
        Client client = new Client();
        Mockito.when(applicationRepository.save(ArgumentMatchers.any())).thenReturn(new Application());
        Application application = service.createNew(client);
        assertNotNull(application);
    }

    @Test
    void updateCurrentStatus() {
        ApplicationStatus applicationStatus = ApplicationStatus.APPROVED;
        Application application = new Application()
                .setApplicationStatus(applicationStatus);
        Mockito.when(applicationRepository.save(application)).thenReturn(application);
        service.updateCurrentStatus(applicationStatus, new Application());
        Mockito.verify(applicationRepository, Mockito.times(1)).save(application);
    }

    @Test
    void setAppliedLoanOffer() {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        Application application = new Application()
                .setAppliedOffer(loanOfferDTO);
        Mockito.when(applicationRepository.findApplicationById(ArgumentMatchers.any())).thenReturn(new Application());
        Mockito.when(applicationRepository.save(ArgumentMatchers.any())).thenReturn(application);
        service.setAppliedLoanOffer(loanOfferDTO);
        Mockito.verify(applicationRepository, Mockito.times(1)).save(ArgumentMatchers.any());
        Mockito.verify(applicationRepository, Mockito.times(1)).findApplicationById(ArgumentMatchers.any());

    }

    @Test
    void findById() {
        Mockito.when(applicationRepository.findApplicationById(ArgumentMatchers.any())).thenReturn(new Application().setId(1L));
        Application applicationReturnedByMethod = service.findById(1L);
        assertEquals(applicationReturnedByMethod.getId(), 1L);
        Mockito.verify(applicationRepository, Mockito.times(1)).findApplicationById(1L);
    }

    @Test
    void getCreditByApplication() {
        Mockito.when(applicationRepository.findApplicationById(ArgumentMatchers.any()))
                .thenReturn(new Application()
                        .setId(1L)
                        .setCredit(new Credit()));
        Credit credit = service.getCreditByApplication(new Application());
        assertNotNull(credit);
    }

    @Test
    void setCredit() {
        Application application = new Application()
                .setCredit(new Credit());
        service.setCredit(new Application(), new Credit());
        Mockito.verify(applicationRepository, Mockito.times(1)).save(application);
    }
}