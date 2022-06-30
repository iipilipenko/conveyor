package com.pilipenko.deal.service.impl;

import com.pilipenko.deal.dto.FinishRegistrationRequestDTO;
import com.pilipenko.deal.dto.LoanApplicationRequestDTO;
import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.model.Employment;
import com.pilipenko.deal.repository.ClientRepository;
import com.pilipenko.deal.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Client createNew(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<Client> clients = clientRepository.findByNumberAndSeries(loanApplicationRequestDTO.getPassportNumber(),
                loanApplicationRequestDTO.getPassportSeries());
        Client client = clients.isEmpty() ? new Client() : clients.get(0);
        log.warn("CLIENTS "+ clients.toString());
        client = client.setBirthDate(loanApplicationRequestDTO.getBirthdate())
                .setSeries(loanApplicationRequestDTO.getPassportSeries())
                .setNumber(loanApplicationRequestDTO.getPassportNumber())
                .setEmail(loanApplicationRequestDTO.getEmail())
                .setLastName(loanApplicationRequestDTO.getLastName())
                .setFirstName(loanApplicationRequestDTO.getFirstName())
                .setMiddleName(loanApplicationRequestDTO.getMiddleName());
        log.info(String.format("created new client: %s", client));

        return clientRepository.save(client);
    }

    @Override
    public Client updateWithFinishRegistrationData(Client client, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        client.setAccount(finishRegistrationRequestDTO.getAccount())
                .setMaritalStatus(finishRegistrationRequestDTO.getMartialStatus())
                .setGender(finishRegistrationRequestDTO.getGender())
                .setDependentAmount(finishRegistrationRequestDTO.getDependentAmount())
                .setIssueDate(finishRegistrationRequestDTO.getPassportIssueDate())
                .setIssueBranch(finishRegistrationRequestDTO.getPassportIssueBranch())
                .setEmployment(modelMapper.map(finishRegistrationRequestDTO.getEmployment(), Employment.class));
        log.info(String.format("client updated: %s",client));
        return clientRepository.save(client);
    }

}
