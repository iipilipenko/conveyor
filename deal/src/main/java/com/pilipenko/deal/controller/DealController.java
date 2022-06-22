package com.pilipenko.deal.controller;

import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.dto.LoanOfferDTO;
import com.pilipenko.deal.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.List;

@Validated
@Slf4j
@RestController
public class DealController {

    @Autowired
    private DealService dealService;

    @Operation(summary = "Create and save entity Client, Application, Employment. Post request to" +
            "MS conveyor-application /conveyor/offers to calculate 4 loan offers. Response 4 loan offer DTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sending 4 loan offers DTO",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @PostMapping(value = "/deal/application", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LoanOfferDTO>> getLoanOffers(@RequestBody @Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {

        final List<LoanOfferDTO> offerDTOList = dealService.getLoanOffers(loanApplicationRequestDTO);

        return offerDTOList != null
                ? new ResponseEntity<>(offerDTOList, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/deal/offer", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> updateLoanOffers(@RequestBody @Valid LoanOfferDTO loanOfferDTO) {

        return new ResponseEntity<>(dealService.updateLoanOffers(loanOfferDTO));

    }


}
