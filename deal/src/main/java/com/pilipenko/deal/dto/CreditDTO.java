package com.pilipenko.deal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreditDTO {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Integer term;

    @NotNull
    private BigDecimal monthlyPayment;

    @NotNull
    private BigDecimal rate;

    @NotNull
    private BigDecimal psk;

    @NotNull
    private Boolean isInsuranceEnabled;

    @NotNull
    private Boolean isSalaryClient;

    @NotNull
    private List<PaymentScheduleElement> paymentSchedule;
}
