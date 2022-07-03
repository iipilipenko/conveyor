package com.pilipenko.deal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pilipenko.deal.dto.CreditDTO;
import com.pilipenko.deal.dto.FinishRegistrationRequestDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.dto.ScoringDataDTO;
import com.pilipenko.deal.model.*;
import org.checkerframework.checker.units.qual.A;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.AttributeAccessor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.comparator.Comparators;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private ClientService clientService;

    @Mock
    private CreditService creditService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private StatusHistoryService statusHistoryService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RestTemplateService restTemplate;

    @InjectMocks
    private DealService service;

    @Test
    void getLoanOffers() throws URISyntaxException {
        List<LoanOfferDTO> offers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            offers.add(new LoanOfferDTO().setRate(BigDecimal.valueOf(10 + i)));
        }
        when(applicationService.createNew(ArgumentMatchers.any())).thenReturn(new Application().setId(1L));
        when(restTemplate.postToConveyorOffers(ArgumentMatchers.any())).thenReturn(offers);
        List<LoanOfferDTO> listReturnedByService = service.getLoanOffers(new LoanApplicationRequestDTO());
        assertEquals(listReturnedByService
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(LoanOfferDTO::getRate)))
                .collect(Collectors.toList()), listReturnedByService);
        assertNotNull(listReturnedByService);
    }

    @Test
    void getLoanOffersWithInvalidArgs() throws URISyntaxException {
        when(applicationService.createNew(ArgumentMatchers.any())).thenReturn(null);
        when(restTemplate.postToConveyorOffers(ArgumentMatchers.any())).thenReturn(null);
        List<LoanOfferDTO> listReturnedByService = service.getLoanOffers(new LoanApplicationRequestDTO());
        assertNull(listReturnedByService);
    }

    @Test
    void updateLoanOffers () {
        Credit credit = new Credit();
        Application application = new Application()
                .setId(1L)
                .setCredit(credit);
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(1L);
        Mockito.when(applicationService.findById(ArgumentMatchers.any())).thenReturn(application);
        HttpStatus httpStatus = service.updateLoanOffers(loanOfferDTO);
        assertEquals(HttpStatus.OK, httpStatus);
        Mockito.verify(creditService, Mockito.times(1))
                .updateCreditWithAppliedOffer(ArgumentMatchers.same(credit), ArgumentMatchers.same(loanOfferDTO));
    }

    @Test
    void updateLoanOffersWithInvalidArgs () {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        HttpStatus httpStatus = service.updateLoanOffers(loanOfferDTO);
        assertEquals(HttpStatus.BAD_REQUEST, httpStatus);
    }

    @Test
    void finishCalcValidArgs () throws URISyntaxException {
        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO();
        Long appId = 1L;
        Mockito.when(applicationService.findById(ArgumentMatchers.anyLong())).thenReturn(new Application()
        .setCredit(new Credit())
        .setClient(new Client().setEmployment(new Employment())));
        Mockito.when(restTemplate.postToConveyorCalculation(ArgumentMatchers.any())).thenReturn(new CreditDTO());
        HttpStatus httpStatus = service.finishRegistrationAndCalculatingCredit(finishRegistrationRequestDTO, appId);
        assertEquals(HttpStatus.OK, httpStatus);
    }

    @Test
    void finishCalcInvalidArgs () {
        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO();
        Long appId = 1L;
        Mockito.when(applicationService.findById(ArgumentMatchers.anyLong())).thenReturn(new Application());
        HttpStatus httpStatus = service.finishRegistrationAndCalculatingCredit(finishRegistrationRequestDTO, appId);
        assertEquals(HttpStatus.BAD_REQUEST, httpStatus);
    }




}