package com.pilipenko.deal.model;

import com.pilipenko.deal.enums.EmploymentStatus;
import com.pilipenko.deal.enums.JobPosition;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Entity
@Data
@Accessors(chain = true)
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    private String employer;

    @Pattern(regexp = "[\\d]{12}")
    private String employerINN;

    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private JobPosition position;

    private Integer workExperienceCurrent;

    private Integer workExperienceTotal;

}
