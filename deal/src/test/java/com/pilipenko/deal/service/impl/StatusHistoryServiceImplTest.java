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
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class StatusHistoryServiceImplTest {

    @InjectMocks
    private StatusHistoryServiceImpl service;
    @Mock
    private StatusHistoryRepository repository;


    @Test
    void updateStatus() {
        Application application = new Application();
        service.updateStatus(application, ApplicationStatus.PREAPPROVAL);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any());
    }
}

//    Client client = new Client();
//        client.setBirthDate(LocalDate.of(2022,11,11))
//                .setSeries("7777")
//                .setNumber("777888")
//                .setEmail("test@email.ru")
//                .setLastName("lastName")
//                .setFirstName("FirstName")
//                .setMiddleName("middleName");
//                Application application = new Application()
//                .setClient(client)
//                .setApplicationStatus(ApplicationStatus.PREAPPROVAL)
//                .setCreationDate(LocalDate.of(2022,11,11));
//                StatusHistory currentStatus = new StatusHistory()
//                .setApplicationStatus(ApplicationStatus.PREAPPROVAL)
//                .setLocalDate(LocalDate.now())
//                .setApplication(application);
//                service.updateStatus(application, ApplicationStatus.PREAPPROVAL);
//                Mockito.verify(repository, Mockito.times(1));