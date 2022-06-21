package com.pilipenko.deal.model;

import com.pilipenko.deal.enums.ApplicationStatus;
import com.pilipenko.deal.enums.CreditStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
@Entity
@NoArgsConstructor
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private ApplicationStatus applicationStatus;

    private LocalDate localDate;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;
}
