package com.pilipenko.deal.service;

import com.pilipenko.deal.dto.LoanApplicationRequestDTO;

public interface ClientService {

    Long createNew(LoanApplicationRequestDTO loanApplicationRequestDTO);

}
