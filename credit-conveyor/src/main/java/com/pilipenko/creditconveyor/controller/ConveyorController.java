package com.pilipenko.creditconveyor.controller;

import com.pilipenko.creditconveyor.dto.CreditDTO;
import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import com.pilipenko.creditconveyor.dto.ScoringDataDTO;
import com.pilipenko.creditconveyor.service.ConveyorService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Slf4j
public class ConveyorController {

    @Autowired
    private ConveyorService conveyorService;

    @Operation(summary = "Pre validation and getting 4 loan offers with/without insurance, salary client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sending 4 loan offers DTO",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @PostMapping(value = "/conveyor/offers", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LoanOfferDTO>> getLoanOffers(@RequestBody @Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("New request /conveyor/offers from " + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName());

        final List<LoanOfferDTO> offerDTOList = conveyorService.createLoanOffers(loanApplicationRequestDTO);

        return offerDTOList != null
                ? new ResponseEntity<>(offerDTOList, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Validation, calculate final loan rate and getting final loan offer with payment schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sending final loan offer DTO",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @PostMapping(value = "/conveyor/calculation", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditDTO> getCreditDTO(@RequestBody @Valid ScoringDataDTO scoringDataDTO) {

        log.info("New request /conveyor/calculation from " + scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName());

        final CreditDTO creditDTO = conveyorService.createCreditDTO(scoringDataDTO);

        return creditDTO != null
                ? new ResponseEntity<>(creditDTO, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


}