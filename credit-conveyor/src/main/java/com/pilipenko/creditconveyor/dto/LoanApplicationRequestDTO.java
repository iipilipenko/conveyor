package com.pilipenko.creditconveyor.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanApplicationRequestDTO {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Integer term;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String middleName;

    @Email
    private String email;

//    @NotNull
    private LocalDate birthdate;

    @NotNull
    private String passportSeries;

    @NotNull
    private String passportNumber;

}
