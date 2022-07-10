package com.pilipenko.application.service.Impl;
import com.pilipenko.application.dto.LoanApplicationRequestDTO;
import com.pilipenko.application.dto.LoanOfferDTO;
import com.pilipenko.application.service.RestTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Service
@PropertySource("classpath:creditConveyor.properties")
public class RestTemplateServiceImpl implements RestTemplateService {
    @Value("${deal.application.url}")
    private String dealApplication;

    @Value("${deal.offer.url}")
    private String dealOffer;


    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<LoanOfferDTO> postToConveyorOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) throws URISyntaxException {
        RequestEntity<LoanApplicationRequestDTO> requestEntity = RequestEntity
                .post(new URI(dealApplication))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanApplicationRequestDTO);

        ResponseEntity<List<LoanOfferDTO>> responseEntity = restTemplate.exchange(dealApplication,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<LoanOfferDTO>>() {
                });

        return responseEntity.getBody();
    }

    @Override
    public void postWithAppliedLoanOffer(LoanOfferDTO loanOfferDTO) throws URISyntaxException {
        RequestEntity<LoanOfferDTO> requestEntity = RequestEntity
                .post(new URI(dealOffer))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanOfferDTO);

        restTemplate.put(dealOffer, requestEntity);
    }
}
