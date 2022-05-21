package com.pilipenko.creditconveyor.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmploymentDTO {
              private Enum employmentStatus;
              private String employerINN;
              private BigDecimal salary;
              private Enum position;
              private Integer workExperienceTotal;
              private Integer workExperienceCurrent;
}
