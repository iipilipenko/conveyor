package com.pilipenko.deal.model;

import com.pilipenko.deal.dto.PaymentScheduleElement;
import com.pilipenko.deal.enums.CreditStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
public class Credit {

    @Id
    private Long id;

    private BigDecimal amount;

    private Integer term;

    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    private BigDecimal psk;

    @OneToMany(mappedBy = "credit")
    private List<PaymentSchedule> paymentSchedule;

    private Boolean isInsuranceEnabled;

    private Boolean isSalaryClient;

    private CreditStatus creditStatus;
}
