package com.pilipenko.creditconveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PaymentScheduleElement {

    @NotNull
    private int number;

    @NotNull
    private LocalDate date;

    @NotNull
    private BigDecimal totalPayment;

    @NotNull
    private BigDecimal interestPayment;

    @NotNull
    private BigDecimal debtPayment;

    @NotNull
    private BigDecimal remainingDebt;

}
