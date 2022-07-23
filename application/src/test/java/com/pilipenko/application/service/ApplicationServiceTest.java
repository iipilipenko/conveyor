package com.pilipenko.application.service;

import com.pilipenko.application.dto.LoanApplicationRequestDTO;
import com.pilipenko.application.dto.LoanOfferDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private RestTemplateService restTemplateService;


    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void createLoanOffers() throws URISyntaxException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        List<LoanOfferDTO> loanOfferDTO = new ArrayList<>();
        when(restTemplateService.postToConveyorOffers(loanApplicationRequestDTO)).thenReturn(loanOfferDTO);
        List<LoanOfferDTO> resultFromApplicationService = applicationService.createLoanOffers(loanApplicationRequestDTO);
        assertEquals(loanOfferDTO, resultFromApplicationService);
    }

    @Test
    void createLoanOffersThrowsException() throws URISyntaxException{
        when(restTemplateService.postToConveyorOffers(ArgumentMatchers.any())).thenThrow(new URISyntaxException("", "reason"));
        List<LoanOfferDTO> resultFromApplicationService = applicationService.createLoanOffers(new LoanApplicationRequestDTO());
        assertNull(resultFromApplicationService);
    }

    @Test
    void setAppliedOffer() throws URISyntaxException{
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        applicationService.setAppliedOffer(loanOfferDTO);
        Mockito.verify(restTemplateService, Mockito.times(1)).postWithAppliedLoanOffer(loanOfferDTO);
    }


}