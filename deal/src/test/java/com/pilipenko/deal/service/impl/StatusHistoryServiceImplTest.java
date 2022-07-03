package com.pilipenko.deal.service.impl;

import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.repository.StatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class StatusHistoryServiceImplTest {

    @Mock
    private StatusHistoryRepository repository;

    @InjectMocks
    private StatusHistoryServiceImpl service = new StatusHistoryServiceImpl();

    @Test
    void updateStatus() {
        Application application = new Application();
        service.updateStatus(application, ApplicationStatus.PREAPPROVAL);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any());
    }
}