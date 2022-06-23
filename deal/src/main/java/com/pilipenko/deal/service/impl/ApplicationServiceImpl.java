package com.pilipenko.deal.service.impl;

import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.repository.ApplicationRepository;
import com.pilipenko.deal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Application createNew(Client client) {
        Application application = new Application()
                .setClient(client)
                .setApplicationStatus(ApplicationStatus.PREAPPROVAL)
                .setCreationDate(LocalDate.now());
        log.info(String.format("created new application: %s", application));

        return applicationRepository.save(application);
    }

    @Override
    public void updateCurrentStatus(ApplicationStatus applicationStatus, Application application) {
//        application.setApplicationStatus(applicationStatus);
        Application application1 = applicationRepository.findApplicationById(application.getId());
        application1.setApplicationStatus(applicationStatus);
        applicationRepository.save(application1);
        log.info(String.format("current application status updated: %s", application1));
    }

    @Override
    public void setAppliedLoanOffer(LoanOfferDTO loanOfferDTO) {
        Application application = applicationRepository.findApplicationById(loanOfferDTO.getApplicationId());
        application.setAppliedOffer(loanOfferDTO);
        applicationRepository.save(application);
        log.info(String.format("Set applied loan offer into application: %s", application));
    }

    @Override
    public Application findById(Long id) {
        Application application = applicationRepository.findApplicationById(id);
        log.info(String.format("find application: %s", application));
        if (application == null) {
            log.error("is null");
        } else {
            log.info("not null");
        }
        return application;
    }

    @Override
    public Credit getCreditByApplication(Application application) {
        return applicationRepository.findApplicationById(application.getId()).getCredit();
    }

    @Override
    public void setCredit(Application application, Credit credit) {
        application.setCredit(credit);
        log.info("Set credit to application " + application.getId() + " :" + credit.toString());
    }
}