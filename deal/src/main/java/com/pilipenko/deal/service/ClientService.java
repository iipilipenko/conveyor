package com.pilipenko.deal.service;

import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.model.ClientID;

public interface ClientService {

    Long createNew(LoanApplicationRequestDTO loanApplicationRequestDTO);

}
