package com.pilipenko.creditconveyor.dto;

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

    @NotNull
    private Long applicationId;

    @NotNull
    private BigDecimal requestedAmount;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private Integer term;

    @NotNull
    private BigDecimal monthlyPayment;

    @NotNull
    @Min(value = 0)
    private BigDecimal rate;

    @NotNull
    @BooleanFlag
    private Boolean isInsuranceEnabled;

    @NotNull
    @BooleanFlag
    private Boolean isSalaryClient;
}
