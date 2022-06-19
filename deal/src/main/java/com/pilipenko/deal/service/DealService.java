package com.pilipenko.deal.service;


import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.dto.LoanOfferDTO;
import com.pilipenko.deal.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DealService {

    @Autowired
    private ClientService clientService;

    public List<LoanOfferDTO> getLoanOffers (LoanApplicationRequestDTO loanApplicationRequestDTO) {

        clientService.createNew(loanApplicationRequestDTO);

        return null;
    }
}
