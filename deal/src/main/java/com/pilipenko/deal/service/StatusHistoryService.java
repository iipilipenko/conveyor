package com.pilipenko.deal.service;

import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.StatusHistory;

import java.util.List;

public interface StatusHistoryService {
    public void updateStatus(Application application, ApplicationStatus applicationStatus);
}
