package com.pilipenko.creditconveyor.controller;

import com.pilipenko.creditconveyor.dto.CreditDTO;
import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import com.pilipenko.creditconveyor.dto.ScoringDataDTO;
import com.pilipenko.creditconveyor.service.ConveyorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConveyorController {

    @Autowired
    private ConveyorService conveyorService;

    @PostMapping(value = "/conveyor/offers", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LoanOfferDTO>> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        final List<LoanOfferDTO> offerDTOList = conveyorService.createLoanOffers(loanApplicationRequestDTO);

        return offerDTOList != null
                ? new ResponseEntity<>(offerDTOList, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/conveyor/calculation", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditDTO> getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO) {
        final CreditDTO creditDTO  = conveyorService.createCreditDTO(scoringDataDTO);

        return creditDTO != null
                ? new ResponseEntity<>(creditDTO, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


}