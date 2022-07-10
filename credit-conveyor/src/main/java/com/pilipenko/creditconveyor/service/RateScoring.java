package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.enums.EmploymentStatus;
import com.pilipenko.creditconveyor.enums.Gender;
import com.pilipenko.creditconveyor.enums.JobPosition;
import com.pilipenko.creditconveyor.enums.MartialStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
@AllArgsConstructor
public class RateScoring {

    @Autowired
    private ConveyorConfigurationParams conveyorConfigurationParams;

    public Double calculateRate(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
                                MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
                                Gender gender, int currentWorkingExperience, int totalWorkingExperience,
                                boolean isInsuranceEnabled, boolean isSalaryClient, BigDecimal loanAmount, Long requestId) {

        Double currentRate = conveyorConfigurationParams.getBaseRate();

        log.info(requestId + " Base rate is: " + currentRate);

        switch (employmentStatus) {
            case WORKING:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusWorkingOnTheRate();
                log.info(requestId + " Rate increased by 0.5 - worker");
                break;
            case ENTREPRENEUR:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusEntrepreneurOnTheRate();
                log.info(requestId + " Rate increased by 3 - entrepreneur");
                break;
            case SELF_EMPLOYED:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusSelfEmployedOnTheRate();
                log.info(requestId + " Rate increased by 1 - self employed");
                break;
            case JOBLESS:
            default:
                log.warn("Denial of a loan - jobless ");
                return null;

        }
        switch (jobPosition) {
            case JUNIOR:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusJuniorOnTheRate();
                log.info(requestId + " Rate increased by 1 - junior");
                break;
            case MIDDLE:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusMiddleOnTheRate();
                log.info(requestId + " Rate decreased by 1 - middle");
                break;
            case SENIOR:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusSeniorOnTheRate();
                log.info(requestId + " Rate decreased by 2 - senior");
                break;
            default:
                log.warn("Denial of a loan - incorrect work position");
                return null;
        }

        if (salary.multiply(BigDecimal.valueOf(conveyorConfigurationParams.getMultiplierMaxNumberOfSalariesComparisonWithRequestedAmount()))
                .compareTo(loanAmount) < 0) {
            log.warn("Denial of a loan - loan > 20 x salaries");
            return null;
        }
        switch (martialStatus) {
            case MARRIED:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusMarriedOnTheRate();
                log.info(requestId + " Rate decreased by 3 - married");
                break;
            case SINGLE:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusSingleOnTheRate();
                log.info(requestId + " Rate increased by 1 - single");
                break;
            case DIVORCE:
                log.info(requestId + " Rate increased by 3 - divorce");
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusDivorceOnTheRate();
                break;
            default:
                log.warn("Denial of a loan - incorrect marital status");
                return null;
        }
        if (dependentAmount > conveyorConfigurationParams.getMinCountDependentAmountWillInfluenceRate()) {
            currentRate += conveyorConfigurationParams.getInfluenceOfDependentAmountOnTheRate();
            log.info(requestId + " Rate increased by 1 - dependent amount > 1");
        }
        if (Period.between(birthDate, LocalDate.now()).getYears() < conveyorConfigurationParams.getMinAge() ||
                Period.between(birthDate, LocalDate.now()).getYears() > conveyorConfigurationParams.getMaxAge()) {
            log.warn("Denial of a loan - <18 or >60 y.o.>");
            return null;
        }
        if (gender == Gender.MALE
                && (Period.between(birthDate, LocalDate.now()).getYears() > conveyorConfigurationParams.getMinMaleReliableAge()
                || Period.between(birthDate, LocalDate.now()).getYears() < conveyorConfigurationParams.getMaxMaleReliableAge())) {
            currentRate += conveyorConfigurationParams.getInfluenceReliableAgeOnRate();
            log.info(requestId + " Rate decreased by 3 - male >30 <55 y.o");
        }
        if (gender == Gender.FEMALE
                && (Period.between(birthDate, LocalDate.now()).getYears() > conveyorConfigurationParams.getMaxFemaleReliableAge()
                || Period.between(birthDate, LocalDate.now()).getYears() < conveyorConfigurationParams.getMaxFemaleReliableAge())) {
            currentRate += conveyorConfigurationParams.getInfluenceReliableAgeOnRate();
            log.info(requestId + " Rate decreased by 3 - female >35 <60 y.o");
        }
        if (gender == Gender.OTHER) {
            log.info(requestId + " Rate increased by 3 - just because non binary gender");
            currentRate += conveyorConfigurationParams.getInfluenceOfGenderOtherOnRate();
        }
        if (currentWorkingExperience < conveyorConfigurationParams.getMinCurrentWorkExperience()
                || totalWorkingExperience < conveyorConfigurationParams.getMinTotalWorkExperience()) {
            log.warn("Denial of a loan - working exp current <3y or working exp total <12");
            return null;
        }
        if (isInsuranceEnabled) {
            currentRate += conveyorConfigurationParams.getInfluenceOfInsuranceOnTheRate();
            log.info(requestId + " Rate decreased by 3 - insurance enabled");
        }
        if (isSalaryClient) {
            log.info(requestId + " Rate decreased by 1 - is salary client");
            currentRate += conveyorConfigurationParams.getInfluenceOfSalaryClientOnTheRate();
        }
        log.info(requestId + " rate after scoring is " + currentRate);
        return currentRate;
    }
}
