package com.pilipenko.creditconveyor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pilipenko.creditconveyor.dto.*;
import com.pilipenko.creditconveyor.enums.EmploymentStatus;
import com.pilipenko.creditconveyor.enums.Gender;
import com.pilipenko.creditconveyor.enums.JobPosition;
import com.pilipenko.creditconveyor.enums.MartialStatus;
import com.pilipenko.creditconveyor.service.ConveyorService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest
@AutoConfigureMockMvc

class ConveyorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConveyorService conveyorService;

    private static ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Test
    void getLoanOffersEndpointTestWithValidArgs() throws Exception {

        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
                10, "Igor", "Pilipenko", "Igorevich",
                "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
                "4444", "666666");

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO(1000L, BigDecimal.valueOf(20000), BigDecimal.valueOf(30000),
                (Integer) 8, BigDecimal.valueOf(1000), BigDecimal.valueOf(20), true, true);

        List<LoanOfferDTO> offers = new ArrayList<>();
        offers.add(0, loanOfferDTO);
        offers.add(1, loanOfferDTO);
        offers.add(2, loanOfferDTO);
        offers.add(3, loanOfferDTO);

        Mockito.when(conveyorService.createLoanOffers(ArgumentMatchers.any())).thenReturn(offers);

        String json = mapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/conveyor/offers").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void getLoanOffersEndpointTestWithInvalidArgs() throws Exception {

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO(1000L, BigDecimal.valueOf(20000), BigDecimal.valueOf(30000),
                (Integer) 8, BigDecimal.valueOf(1000), BigDecimal.valueOf(20), true, true);

        List<LoanOfferDTO> offers = new ArrayList<>();
        offers.add(0, loanOfferDTO);
        offers.add(1, loanOfferDTO);
        offers.add(2, loanOfferDTO);
        offers.add(3, loanOfferDTO);

        Mockito.when(conveyorService.createLoanOffers(ArgumentMatchers.any())).thenReturn(offers);

        String json = mapper.writeValueAsString(null);

        mockMvc.perform(post("/conveyor/offers").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(json).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void getCreditDTOEndpointTestWithValidArgs() throws Exception {
        EmploymentDTO employmentDTO = new EmploymentDTO(EmploymentStatus.WORKING, "123456789012",
                BigDecimal.valueOf(100000), JobPosition.JUNIOR, 20, 20);
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(100000), 6, "Igor",
                "Pilipenko", "Igorevich", Gender.MALE, LocalDate.of(1993, 4, 21),
                "3333", "333333", LocalDate.of(2030, 4, 21),
                "test", MartialStatus.MARRIED, 0, employmentDTO, "test",
                true, true);
        CreditDTO creditDTO = new CreditDTO();

        Mockito.when(conveyorService.createCreditDTO(ArgumentMatchers.any())).thenReturn(creditDTO);

        String json = mapper.writeValueAsString(scoringDataDTO);

        mockMvc.perform(post("/conveyor/calculation").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(json).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getCreditDTOEndpointTestWithInvalidArgs() throws Exception {
        ScoringDataDTO scoringDataDTO = null;
        CreditDTO creditDTO = new CreditDTO();

        Mockito.when(conveyorService.createCreditDTO(ArgumentMatchers.any())).thenReturn(creditDTO);

        String json = mapper.writeValueAsString(null);

        mockMvc.perform(post("/conveyor/calculation").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(json).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}