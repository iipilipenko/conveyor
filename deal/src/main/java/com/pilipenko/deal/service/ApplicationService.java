package com.pilipenko.deal.service;

import com.pilipenko.deal.dto.LoanOfferDTO;
import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.Client;

public interface ApplicationService {
    Application createNew(Client client);

    void updateCurrentStatus (ApplicationStatus applicationStatus, Application application);

    void setAppliedLoanOffer (LoanOfferDTO loanOfferDTO);

    Application findById(Long id);
}
