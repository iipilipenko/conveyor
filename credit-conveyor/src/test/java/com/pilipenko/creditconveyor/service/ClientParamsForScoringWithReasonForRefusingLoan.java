package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.enums.EmploymentStatus;
import com.pilipenko.creditconveyor.enums.Gender;
import com.pilipenko.creditconveyor.enums.JobPosition;
import com.pilipenko.creditconveyor.enums.MartialStatus;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

public class ClientParamsForScoringWithReasonForRefusingLoan implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                        //refuse reason - jobless
                Arguments.of(EmploymentStatus.JOBLESS, JobPosition.JUNIOR, BigDecimal.valueOf(0), MartialStatus.DIVORCE,
                        3, LocalDate.now().minusYears((long) 18).plusDays((long) 1), Gender.MALE, 2, 11,
                        true, true),
                //refuse reason - loan > 20 salaries
                Arguments.of(EmploymentStatus.WORKING, JobPosition.JUNIOR, BigDecimal.valueOf(0), MartialStatus.DIVORCE,
                        3, LocalDate.now().minusYears((long) 18).plusDays((long) 1), Gender.MALE, 2, 11,
                        true, true),
                //refuse reason - <18
                Arguments.of(EmploymentStatus.WORKING, JobPosition.JUNIOR, BigDecimal.valueOf(Integer.MAX_VALUE),
                        MartialStatus.DIVORCE, 3, LocalDate.now().minusYears((long) 18).plusDays((long) 1),
                        Gender.MALE, 2, 11, true, true),
                //refuse reason - >60
                Arguments.of(EmploymentStatus.WORKING, JobPosition.JUNIOR, BigDecimal.valueOf(Integer.MAX_VALUE),
                        MartialStatus.DIVORCE, 3, LocalDate.now().minusYears((long) 60).minusDays((long) 1),
                        Gender.MALE, 2, 11, true, true)
                );
    }
}
