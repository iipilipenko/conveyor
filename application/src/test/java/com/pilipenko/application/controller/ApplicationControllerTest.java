package com.pilipenko.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pilipenko.application.dto.LoanApplicationRequestDTO;
import com.pilipenko.application.dto.LoanOfferDTO;
import com.pilipenko.application.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest
@AutoConfigureMockMvc
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService service;

    private static ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Test
    void getLoanOffers() throws Exception {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
                10, "Igor", "Pilipenko", "Igorevich",
                "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
                "4444", "666666");
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        List<LoanOfferDTO> offersList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            offersList.add(loanOfferDTO);
        }
        Mockito.when(service.createLoanOffers(ArgumentMatchers.any())).thenReturn(offersList);
        String jsonRequest = mapper.writeValueAsString(requestDTO);
        String jsonResponse = mapper.writeValueAsString(offersList);
        MvcResult result = mockMvc.perform(post("/application").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(jsonRequest).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertEquals(jsonResponse, result.getResponse().getContentAsString());
    }

    @Test
    void getLoanOffersWhenServiceReturnsNull() throws Exception {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
                10, "Igor", "Pilipenko", "Igorevich",
                "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
                "4444", "666666");
        Mockito.when(service.createLoanOffers(ArgumentMatchers.any())).thenReturn(null);
        String jsonRequest = mapper.writeValueAsString(requestDTO);
        MvcResult result = mockMvc.perform(post("/application").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(jsonRequest).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    void updateLoanOffers() throws Exception {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO(1000L, BigDecimal.valueOf(20000), BigDecimal.valueOf(30000),
                (Integer) 8, BigDecimal.valueOf(1000), BigDecimal.valueOf(20), true, true);
        String jsonRequest = mapper.writeValueAsString(loanOfferDTO);
        MvcResult result = mockMvc.perform(put("/application/offer").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(jsonRequest).accept(MediaType.APPLICATION_JSON)).andReturn();
        Mockito.verify(service, Mockito.times(1)).setAppliedOffer(loanOfferDTO);
    }
}