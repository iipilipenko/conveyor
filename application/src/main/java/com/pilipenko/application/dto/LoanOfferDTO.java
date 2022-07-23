package com.pilipenko.application.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferDTO {

    @Schema(description = "Loan application ID", example = "2")
    @NotNull
    private Long applicationId;

    @Schema(description = "Requested amount", example = "100000")
    @NotNull
    private BigDecimal requestedAmount;

    @Schema(description = "Total amount", example = "110000")
    @NotNull
    private BigDecimal totalAmount;

    @Schema(description = "Term", example = "10")
    @NotNull
    private Integer term;

    @Schema(description = "Monthly payment", example = "12466.667")
    @NotNull
    private BigDecimal monthlyPayment;

    @Schema(description = "Rate", example = "16")
    @NotNull
    @Min(value = 0)
    private BigDecimal rate;

    @Schema(description = "Insurance", example = "true")
    @NotNull
    @BooleanFlag
    private Boolean isInsuranceEnabled;

    @Schema(description = "Salary client", example = "true")
    @NotNull
    @BooleanFlag
    private Boolean isSalaryClient;
}

