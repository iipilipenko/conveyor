package com.pilipenko.deal.service;


import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.dto.LoanOfferDTO;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@PropertySource("classpath:deal.properties")
public class DealService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${conveyor.offers.url}")
    private String conveyorOffersUrl;

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
            try {
                clientService.createNew(loanApplicationRequestDTO);

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

                List<LoanOfferDTO> offersList = responseEntity.getBody();

            } catch (URISyntaxException | NullPointerException e) {
                log.error(e.getMessage());
            }
        return null;
    }
}
