package com.pilipenko.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pilipenko.deal.dto.EmploymentDTO;
import com.pilipenko.deal.dto.FinishRegistrationRequestDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.enums.EmploymentStatus;
import com.pilipenko.deal.enums.Gender;
import com.pilipenko.deal.enums.JobPosition;
import com.pilipenko.deal.enums.MartialStatus;
import com.pilipenko.deal.model.Employment;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.service.DealService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@Slf4j
@WebMvcTest
@AutoConfigureMockMvc
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    private static ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Test
    void getLoanOffersEndpointWithValidArgs() throws Exception {
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
        Mockito.when(dealService.getLoanOffers(ArgumentMatchers.any())).thenReturn(offers);
        String jsonRequest = mapper.writeValueAsString(requestDTO);
        String jsonResponse = mapper.writeValueAsString(offers);
        MvcResult result = mockMvc.perform(post("/deal/application").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(jsonRequest).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertEquals(jsonResponse, result.getResponse().getContentAsString());
    }

    @Test
    void getLoanOffersEndpointWithInvalidArgs() throws Exception {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
                10, "Igor", "Pilipenko", "Igorevich",
                "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
                "4444", "666666");
        Mockito.when(dealService.getLoanOffers(ArgumentMatchers.any())).thenReturn(null);
        String jsonRequest = mapper.writeValueAsString(requestDTO);
        MvcResult result = mockMvc.perform(post("/deal/application").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(jsonRequest).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    void acceptLoanOfferMethodIsUsed () throws Exception {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO(1000L, BigDecimal.valueOf(20000), BigDecimal.valueOf(30000),
                (Integer) 8, BigDecimal.valueOf(1000), BigDecimal.valueOf(20), true, true);
        Mockito.when(dealService.updateLoanOffers(ArgumentMatchers.any())).thenReturn(HttpStatus.OK);
        String jsonRequest = mapper.writeValueAsString(loanOfferDTO);
        mockMvc.perform(put("/deal/offer").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(jsonRequest).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(dealService, Mockito.times(1)).updateLoanOffers(loanOfferDTO);
    }

    @Test
    void finishCalculationMethodIsUsed () throws Exception {
        EmploymentDTO employment = new EmploymentDTO(EmploymentStatus.SELF_EMPLOYED, "123456789012", BigDecimal.valueOf(100000),
                JobPosition.JUNIOR, 123, 123);
        FinishRegistrationRequestDTO requestDTO = new FinishRegistrationRequestDTO(Gender.FEMALE, MartialStatus.MARRIED, 1,
                LocalDate.of(3000, 06, 30), "36006", employment, "account");
        Mockito.when(dealService.finishRegistrationAndCalculatingCredit(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(HttpStatus.OK);
        String jsonRequest = mapper.writeValueAsString(requestDTO);
        mockMvc.perform(put("/deal/calculate/{applicationId}", "1").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(jsonRequest).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(dealService, Mockito.times(1)).finishRegistrationAndCalculatingCredit(requestDTO, 1L);
    }
}