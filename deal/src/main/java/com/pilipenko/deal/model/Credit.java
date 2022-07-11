package com.pilipenko.deal.model;

import com.pilipenko.deal.dto.PaymentScheduleElement;
import com.pilipenko.deal.enums.CreditStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@RequiredArgsConstructor
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;
}
