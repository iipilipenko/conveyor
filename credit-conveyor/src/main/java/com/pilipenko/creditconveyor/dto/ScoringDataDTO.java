package com.pilipenko.creditconveyor.dto;

import com.pilipenko.creditconveyor.enums.Gender;
import com.pilipenko.creditconveyor.enums.MartialStatus;
import com.pilipenko.creditconveyor.validation.Adult;
import com.pilipenko.creditconveyor.validation.MiddleNameEpsonOrCorrect;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScoringDataDTO {

    @NotNull
    @DecimalMin(value = "10000", inclusive = true)
    private BigDecimal amount;

    @NotNull
    @Min(value = 6, message = "Term should not be less than 6 month")
    private Integer term;

    @NotNull
    @Pattern(regexp = "[a-zA-Z]{2,30}")
    private String firstName;

    @NotNull
    @Pattern(regexp = "[a-zA-Z]{2,30}")
    private String lastName;

    @MiddleNameEpsonOrCorrect
    private String middleName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull
    @Adult
    private LocalDate birthdate;

    @NotNull
    @Pattern(regexp = "[\\d]{4}", message = "Passport series should have 4 digits")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "[\\d]{6}", message = "Passport number should have 6 digits")
    private String passportNumber;

    @NotNull
    @Future
    private LocalDate passportIssueDate;

    @NotNull
    private String passportIssueBranch;

    @Enumerated(EnumType.STRING)
    private MartialStatus maritalStatus;

    @Min(value = 0, message = "Dependent amount should be 0 or greater")
    private Integer dependentAmount;

    @NotNull
    private EmploymentDTO employment;

    @NotNull
    private String account;

    @NotNull
    private Boolean isInsuranceEnabled;

    @NotNull
    private Boolean isSalaryClient;
}
