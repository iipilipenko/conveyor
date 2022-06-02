package com.pilipenko.creditconveyor.dto;

import com.pilipenko.creditconveyor.validation.Adult;
import com.pilipenko.creditconveyor.validation.MiddleNameEpsonOrCorrect;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Constraint;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LoanApplicationRequestDTO {

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

    @NotNull
    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}")
    private String email;

    @NotNull
    @Adult
    private LocalDate birthdate;

    @NotNull
    @Pattern(regexp = "[\\d]{4}", message = "Passport series should have 4 digits")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "[\\d]{6}", message = "Passport number should have 6 digits")
    private String passportNumber;

}
