package com.pilipenko.deal.service.impl;


import com.pilipenko.deal.dto.EmploymentDTO;
import com.pilipenko.deal.dto.FinishRegistrationRequestDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.enums.EmploymentStatus;
import com.pilipenko.deal.enums.Gender;
import com.pilipenko.deal.enums.JobPosition;
import com.pilipenko.deal.enums.MartialStatus;
import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.model.Employment;
import com.pilipenko.deal.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private final ClientServiceImpl clientService = new ClientServiceImpl();


    @Test
    void createNewClientNotExistInRepo() {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
                10, "Igor", "Pilipenko", "Igorevich",
                "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
                "4444", "666666");
        Client clientReturnedByMock = new Client()
                .setBirthDate(requestDTO.getBirthdate())
                .setSeries(requestDTO.getPassportSeries())
                .setNumber(requestDTO.getPassportNumber())
                .setEmail(requestDTO.getEmail())
                .setLastName(requestDTO.getLastName())
                .setFirstName(requestDTO.getFirstName())
                .setMiddleName(requestDTO.getMiddleName());
        Mockito.when(clientRepository.findByNumberAndSeries(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new ArrayList<Client>());
        Mockito.when(clientRepository.save(ArgumentMatchers.any())).thenReturn(clientReturnedByMock);
        Client clientReturnedByService = clientService.createNew(requestDTO);
        assertEquals(clientReturnedByService, clientReturnedByMock);
    }


    //todo check why test for second method does not work
    @Test
    void updatedWithFinishRegistrationData() {
        Employment employment = new Employment();
        FinishRegistrationRequestDTO requestDTO = new FinishRegistrationRequestDTO(Gender.FEMALE, MartialStatus.MARRIED, 1,
                LocalDate.of(3000, 06, 30), "36006", new EmploymentDTO(), "account");
        Client client = new Client();
        Client clientReturnedByMock = new Client()
                .setAccount(requestDTO.getAccount())
                .setMaritalStatus(requestDTO.getMartialStatus())
                .setGender(requestDTO.getGender())
                .setDependentAmount(requestDTO.getDependentAmount())
                .setIssueDate(requestDTO.getPassportIssueDate())
                .setIssueBranch(requestDTO.getPassportIssueBranch())
                .setEmployment(employment);
        Mockito.when(modelMapper.map(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(employment);
        Mockito.when(clientRepository.save(ArgumentMatchers.any())).thenReturn(clientReturnedByMock);
        clientService.updateWithFinishRegistrationData(client, requestDTO);
        Mockito.verify(clientRepository, Mockito.times(1)).save(clientReturnedByMock);
    }


}