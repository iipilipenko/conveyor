package com.pilipenko.deal.service;

import com.pilipenko.deal.dto.FinishRegistrationRequestDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.model.Client;

public interface ClientService {

    Client createNew(LoanApplicationRequestDTO loanApplicationRequestDTO);

    Client updateWithFinishRegistrationData (Client client, FinishRegistrationRequestDTO finishRegistrationRequestDTO);

}
