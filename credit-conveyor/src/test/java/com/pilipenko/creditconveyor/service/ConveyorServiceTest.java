package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import com.pilipenko.creditconveyor.dto.ScoringDataDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ConveyorServiceTest {

    @MockBean
    ScoringDataDTO scoringDataDTO;

    @Autowired
    private ConveyorService conveyorService;

    @Test
    void createLoanOffers() {

        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
                10, "Igor", "Pilipenko", null,
                "iipilipenko@mail.ru", LocalDate.of(1993,8,21),
                "4444", "666666");

        List<LoanOfferDTO> loanOfferDTOList = conveyorService.createLoanOffers(requestDTO);

        Assert.notNull(loanOfferDTOList,"loan offers list must not be null");
        Assert.isTrue(loanOfferDTOList.size()==4, "list should have 4 loan offers");
        for (int i=1; i<loanOfferDTOList.size(); i++) {
            Assert.isTrue(loanOfferDTOList.get(i).getRate().compareTo(loanOfferDTOList.get(i-1).getRate()) < 0,
                    "loan offers should be placed descending order");
        }
    }

    @Test
    void createCreditDTO() {
    }
}