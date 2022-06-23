package com.pilipenko.deal.service;

import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.Client;

public interface ApplicationService {
    Application createNew(Client client);

    void updateCurrentStatus (ApplicationStatus applicationStatus, Application application);

    void setAppliedLoanOffer (LoanOfferDTO loanOfferDTO);

    void setCredit (Application application, Credit credit);

    Application findById(Long id);

    Credit getCreditByApplication(Application application);
}
