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

public class ClientParamsForScoringRate17 implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(EmploymentStatus.ENTREPRENEUR, JobPosition.MIDDLE, BigDecimal.valueOf(Integer.MAX_VALUE), MartialStatus.SINGLE,
                        3, LocalDate.now().minusYears((long) 30).minusDays((long) 1), Gender.MALE, 12, 12,
                        true, true),
                Arguments.of(EmploymentStatus.SELF_EMPLOYED, JobPosition.SENIOR, BigDecimal.valueOf(Integer.MAX_VALUE), MartialStatus.DIVORCE,
                        4, LocalDate.now().minusYears((long) 35).minusDays((long) 1), Gender.FEMALE, 12, 12,
                        true, false),
                Arguments.of(EmploymentStatus.SELF_EMPLOYED, JobPosition.MIDDLE, BigDecimal.valueOf(Integer.MAX_VALUE), MartialStatus.MARRIED,
                        0, LocalDate.now().minusYears((long) 30).minusDays((long) 1), Gender.OTHER, 12, 12,
                        true, false)
        );
    }
}
