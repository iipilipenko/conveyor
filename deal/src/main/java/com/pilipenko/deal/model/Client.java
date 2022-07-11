package com.pilipenko.deal.model;

import com.pilipenko.deal.enums.Gender;
import com.pilipenko.deal.enums.MartialStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Data
@Accessors(chain = true)
@Entity
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String series;

    private String number;

    private LocalDate issueDate;

    private String issueBranch;

    private String lastName;

    private String firstName;

    private String middleName;

    private LocalDate birthDate;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MartialStatus maritalStatus;

    private Integer dependentAmount;

    @OneToOne(cascade = CascadeType.ALL)
    private Employment employment;

    private String account;

}
