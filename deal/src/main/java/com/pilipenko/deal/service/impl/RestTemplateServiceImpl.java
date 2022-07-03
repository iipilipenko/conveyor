package com.pilipenko.deal.service.impl;

import com.pilipenko.deal.dto.CreditDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.dto.ScoringDataDTO;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.service.RestTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Service
@PropertySource("classpath:deal.properties")
public class RestTemplateServiceImpl implements RestTemplateService {

    @Value("${conveyor.offers.url}")
    private String conveyorOffersUrl;

    @Value("${conveyor.calculation.url}")
    private String conveyorCalculationUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<LoanOfferDTO> postToConveyorOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) throws URISyntaxException {
        RequestEntity<LoanApplicationRequestDTO> requestEntity = RequestEntity
                .post(new URI(conveyorOffersUrl))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanApplicationRequestDTO);

        ResponseEntity<List<LoanOfferDTO>> responseEntity = restTemplate.exchange(conveyorOffersUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<LoanOfferDTO>>() {
                });

        return responseEntity.getBody();
    }

    @Override
    public CreditDTO postToConveyorCalculation(ScoringDataDTO scoringDataDTO) throws URISyntaxException {
        RequestEntity<ScoringDataDTO> requestCalculation = RequestEntity
                .post(new URI(conveyorCalculationUrl))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(scoringDataDTO);

        ResponseEntity<CreditDTO> responseEntity = restTemplate.exchange(conveyorCalculationUrl,
                HttpMethod.POST,
                requestCalculation,
                CreditDTO.class);
        return responseEntity.getBody();
    }
}
