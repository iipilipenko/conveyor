package com.pilipenko.creditconveyor.dto;

import com.pilipenko.creditconveyor.enums.EmploymentStatus;
import com.pilipenko.creditconveyor.enums.JobPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDTO {

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    @Pattern(regexp = "[\\d]{12}")
    private String employerINN;

    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private JobPosition position;

    @NotNull
    @Min(value = 0, message = "Total work experience should be 0 or greater")
    private Integer workExperienceTotal;

    @NotNull
    @Min(value = 0, message = "Current work experience should be 0 or greater")
    private Integer workExperienceCurrent;
}
