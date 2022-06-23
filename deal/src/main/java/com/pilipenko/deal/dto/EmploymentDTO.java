package com.pilipenko.deal.dto;

import com.pilipenko.deal.enums.EmploymentStatus;
import com.pilipenko.deal.enums.JobPosition;

import java.math.BigDecimal;

public class EmploymentDTO {

    private EmploymentStatus employmentStatus;

    private String employerINN;

    private BigDecimal salary;

    private JobPosition position;

    private Integer workExperienceTotal;

    private Integer workExperienceCurrent;

}
