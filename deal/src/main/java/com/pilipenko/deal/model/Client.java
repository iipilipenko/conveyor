package com.pilipenko.deal.model;

import com.pilipenko.deal.enums.Gender;
import com.pilipenko.deal.enums.MartialStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Data
@Accessors(chain = true)
@Entity
@IdClass(ClientID.class)
public class Client {

    private Integer series;
    private Integer number;
//    @EmbeddedId
//    private ClientID id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate issueDate;

    private String issueBranch;

    private String lastName;

    private String firstName;

    private String middleName;

    private LocalDate birthDate;

    private String email;

    private Gender gender;

    private MartialStatus maritalStatus;

    private Integer dependentAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employment_id", referencedColumnName = "id")
    private Employment employment;

    private String account;

}
