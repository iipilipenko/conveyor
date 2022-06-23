package com.pilipenko.deal.dto;

import com.pilipenko.deal.enums.Gender;
import com.pilipenko.deal.enums.MartialStatus;
import com.pilipenko.deal.model.Employment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinishRegistrationRequestDTO {

    @NotNull
    private Gender gender;

    @NotNull
    private MartialStatus martialStatus;

    @Enumerated(EnumType.STRING)
    private  Integer dependentAmount;

    @NotNull
    @Future
    private LocalDate passportIssueDate;

    @NotNull
    private String passportIssueBranch;

    @NotNull
    private Employment employment;

    @NotNull
    private String account;

}
