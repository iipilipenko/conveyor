package com.pilipenko.deal.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilipenko.deal.dto.*;
import com.pilipenko.deal.model.*;
import com.pilipenko.deal.enums.ApplicationStatus;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@PropertySource("classpath:deal.properties")
public class DealService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StatusHistoryService statusHistoryService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${conveyor.offers.url}")
    private String conveyorOffersUrl;

    @Value("${conveyor.calculation.url}")
    private String conveyorCalculationUrl;

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        try {
            Client client = clientService.createNew(loanApplicationRequestDTO);
            Application application = applicationService.createNew(client);
            Credit credit = creditService.createNewCredit(loanApplicationRequestDTO);
            applicationService.updateCurrentStatus(ApplicationStatus.PREAPPROVAL, application);
            applicationService.setCredit(application, credit);
            statusHistoryService.updateStatus(application, ApplicationStatus.PREAPPROVAL);

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
            offersList.stream()
                    .sorted(Collections.reverseOrder(Comparator.comparing(LoanOfferDTO::getRate)))
                    .forEach(s -> s.setApplicationId(application.getId()));
            return offersList;
        } catch (URISyntaxException | NullPointerException | HttpClientErrorException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public HttpStatus updateLoanOffers(LoanOfferDTO loanOfferDTO) {
        try {
            Application application = applicationService.findById(loanOfferDTO.getApplicationId());
            Credit credit = application.getCredit();
            creditService.updateTotalCreditAmountWithAppliedOffer(credit, loanOfferDTO);
            applicationService.updateCurrentStatus(ApplicationStatus.APPROVED, application);
            statusHistoryService.updateStatus(application, ApplicationStatus.APPROVED);
            applicationService.setAppliedLoanOffer(loanOfferDTO);
            return HttpStatus.OK;
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
        return HttpStatus.BAD_REQUEST;
    }

    public HttpStatus finishRegistrationAndCalculatingCredit(FinishRegistrationRequestDTO finishRegistrationRequestDTO, Long applicationId) {
        try {
            Application application = applicationService.findById(applicationId);
            Credit credit = application.getCredit();
            Client client = application.getClient();
            clientService.updateWithFinishRegistrationData(client, finishRegistrationRequestDTO);
            Employment employment = client.getEmployment();

            EmploymentDTO employmentDTO = modelMapper.map(employment, EmploymentDTO.class);

            ScoringDataDTO scoringDataDTO = new ScoringDataDTO(
                    credit.getAmount(),
                    credit.getTerm(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getMiddleName(),
                    client.getGender(),
                    client.getBirthDate(),
                    client.getSeries(),
                    client.getNumber(),
                    client.getIssueDate(),
                    client.getIssueBranch(),
                    client.getMaritalStatus(),
                    client.getDependentAmount(),
                    employmentDTO,
                    client.getAccount(),
                    credit.getIsInsuranceEnabled(),
                    credit.getIsSalaryClient());

            log.info(String.format("Scoring data DTO created: %s", scoringDataDTO));

            RequestEntity<ScoringDataDTO> requestCalculation = RequestEntity
                    .post(new URI(conveyorCalculationUrl))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(scoringDataDTO);

            ResponseEntity<CreditDTO> responseEntity = restTemplate.exchange(conveyorCalculationUrl,
                    HttpMethod.POST,
                    requestCalculation,
                    CreditDTO.class);

            CreditDTO creditDTO = responseEntity.getBody();

            log.info(String.format("Received credit DTO: %s", creditDTO));


        } catch (URISyntaxException | NullPointerException | HttpClientErrorException e) {
            log.error(e.getMessage());
        }
        return HttpStatus.BAD_REQUEST;
    }

}
