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

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    private LocalDate localDate;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Override
    public String toString() {
         return "StatusHistory{" +
                "id=" + id +
                ", applicationStatus=" + applicationStatus +
                ", localDate=" + localDate +
                '}';
    }
}
