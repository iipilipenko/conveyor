package com.pilipenko.application.service;

import com.pilipenko.application.dto.LoanApplicationRequestDTO;
import com.pilipenko.application.dto.LoanOfferDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Mockito.when(restTemplateService.postToConveyorOffers(loanApplicationRequestDTO)).thenReturn(loanOfferDTO);
        List<LoanOfferDTO> resultFromApplicationService = applicationService.createLoanOffers(loanApplicationRequestDTO);
        assertEquals(loanOfferDTO, resultFromApplicationService);
    }

    @Test
    void setAppliedOffer() {
    }
}