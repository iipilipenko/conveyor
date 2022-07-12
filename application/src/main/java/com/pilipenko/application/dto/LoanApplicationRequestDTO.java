package com.pilipenko.application.dto;

import com.pilipenko.application.validation.Adult;
import com.pilipenko.application.validation.MiddleNameEpsonOrCorrect;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationRequestDTO {

    @Schema(description = "Loan Amount", example = "100000")
    @NotNull
    @DecimalMin(value = "10000", inclusive = true)
    private BigDecimal amount;

    @Schema(description = "Loan term", example = "10")
    @NotNull
    @Min(value = 6, message = "Term should not be less than 6 month")
    private Integer term;

    @Schema(description = "First name", example = "Igor")
    @NotNull
    @Pattern(regexp = "[a-zA-Z]{2,30}")
    private String firstName;

    @Schema(description = "Last name", example = "Pilipenko")
    @NotNull
    @Pattern(regexp = "[a-zA-Z]{2,30}")
    private String lastName;

    @Schema(description = "Middle name", example = "Igorevich")
    @MiddleNameEpsonOrCorrect
    private String middleName;

    @Schema(description = "Email address", example = "ipilipenko@mail.ru")
    @NotNull
    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}")
    private String email;

    @Schema(description = "Date of birth", example = "1993-07-12")
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
