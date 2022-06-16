package com.pilipenko.deal.controller;

import com.pilipenko.deal.service.DealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@Slf4j
@RestController
public class DealController {

    @Autowired
    private DealService dealService;

    @PostMapping(value = "/deal/application", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LoanOfferDTO>> getLoanOffers(@RequestBody @Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {

        final List<LoanOfferDTO> offerDTOList = dealService.getLoanOffers(loanApplicationRequestDTO);

        return offerDTOList != null
                ? new ResponseEntity<>(offerDTOList, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

//    @PutMapping(value = "/deal/offer", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
//            MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<LoanOfferDTO> getLoanOffers(@RequestBody @Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {
//
//        final List<LoanOfferDTO> offerDTOList = dealService.createLoanOffers(loanApplicationRequestDTO);
//
//        return offerDTOList != null
//                ? new ResponseEntity<>(offerDTOList, HttpStatus.OK)
//                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//    }


}
