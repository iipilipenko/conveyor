package com.pilipenko.deal.model;

import com.pilipenko.deal.dto.LoanOfferDTO;
import com.pilipenko.deal.enums.ApplicationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Client client;

    @OneToOne
    private Credit credit;

    private ApplicationStatus applicationStatus;

    private LocalDate creationDate;

    @OneToOne
    private LoanOfferDTO appliedOffer;

    private LocalDate signDate;

    private String ses_code;

    @OneToMany(mappedBy = "application")
    private List<StatusHistory> statusHistory;


}
