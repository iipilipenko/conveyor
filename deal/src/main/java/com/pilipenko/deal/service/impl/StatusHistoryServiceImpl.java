package com.pilipenko.deal.service.impl;

import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.StatusHistory;
import com.pilipenko.deal.repository.StatusHistoryRepository;
import com.pilipenko.deal.service.ApplicationService;
import com.pilipenko.deal.service.StatusHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatusHistoryServiceImpl implements StatusHistoryService {

    @Autowired
    private StatusHistoryRepository statusHistoryRepository;

    @Override
    public void updateStatus(Application application, ApplicationStatus applicationStatus) {
        StatusHistory currentStatus = new StatusHistory()
                .setApplicationStatus(applicationStatus)
                .setLocalDate(LocalDate.now())
                .setApplication(application);
        log.info(String.format("status history updated: %s", currentStatus));
        statusHistoryRepository.save(currentStatus);
    }
}
