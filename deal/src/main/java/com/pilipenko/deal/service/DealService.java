package com.pilipenko.deal.service;


import com.pilipenko.deal.dto.FinishRegistrationRequestDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.dto.ScoringDataDTO;
import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.Client;
import lombok.extern.slf4j.Slf4j;
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
    private RestTemplate restTemplate;

    @Value("${conveyor.offers.url}")
    private String conveyorOffersUrl;

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
                        .forEach(s-> s.setApplicationId(application.getId()));
                return offersList;
            } catch (URISyntaxException | NullPointerException | HttpClientErrorException e) {
                log.error(e.getMessage());
            }
        return null;
    }

    public HttpStatus updateLoanOffers (LoanOfferDTO loanOfferDTO) {
        try {
            Application application = applicationService.findById(loanOfferDTO.getApplicationId());
            applicationService.updateCurrentStatus(ApplicationStatus.APPROVED, application);
            statusHistoryService.updateStatus(application, ApplicationStatus.APPROVED);
            applicationService.setAppliedLoanOffer(loanOfferDTO);
            return HttpStatus.OK;
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
    return HttpStatus.BAD_REQUEST;
    }

    public HttpStatus finishRegistrationAndCalculatingCredit (FinishRegistrationRequestDTO finishRegistrationRequestDTO, Long applicationId) {
//        try {
//           Application application = applicationService.findById(applicationId);
//           ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
//
//
//        } catch (URISyntaxException | NullPointerException | HttpClientErrorException e) {
//            log.error(e.getMessage());
//        }
//        return HttpStatus.BAD_REQUEST;
        return null;
   }

}
