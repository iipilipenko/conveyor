package com.pilipenko.creditconveyor.service;


import com.pilipenko.creditconveyor.dto.CreditDTO;
import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import com.pilipenko.creditconveyor.dto.PaymentScheduleElement;
import com.pilipenko.creditconveyor.enums.EmploymentStatus;
import com.pilipenko.creditconveyor.enums.Gender;
import com.pilipenko.creditconveyor.enums.JobPosition;
import com.pilipenko.creditconveyor.enums.MartialStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ConveyorConfiguration {

    @Autowired
    private ConveyorConfigurationConstants conveyorConfigurationConstants;

    LoanOfferDTO getLoanOfferDTO(boolean isInsuranceEnabled,
                                 boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO,
                                 Long applicationId) {

        log.info("Pre scoring loan offer insurance: " + isInsuranceEnabled + " salary client: " + isSalaryClient);

        Double rate = conveyorConfigurationConstants.BASE_RATE;
        BigDecimal additionalServicesAmount;
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount();

        if (isSalaryClient) {
            rate += conveyorConfigurationConstants.INFLUENCE_OF_SALARY_CLIENT_ON_THE_RATE;
            log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Rate decreased by 1 - salary client");
        }

        if (isInsuranceEnabled) {
            rate += conveyorConfigurationConstants.INFLUENCE_OF_INSURANCE_ON_THE_RATE;
            log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Rate decreased by 3 - insurance enabled");
            additionalServicesAmount = totalAmount.multiply(BigDecimal.valueOf(conveyorConfigurationConstants.MULTIPLIER_FOR_CALCULATING_INSURANCE_COST))
                    .setScale(2, RoundingMode.HALF_UP);
            log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Insurance coast " + additionalServicesAmount);
            totalAmount = totalAmount.add(additionalServicesAmount).setScale(2, RoundingMode.HALF_UP);
        }
        // formula -> costOfUsingLoan = totalAmount * ( rate% / (12month * 100))
        BigDecimal costOfUsingALoan = totalAmount.multiply(
                new BigDecimal(rate / 1200 * loanApplicationRequestDTO.getTerm())).setScale(2, RoundingMode.HALF_UP);
        log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Cost of using loan " + costOfUsingALoan);

        totalAmount = totalAmount.add(costOfUsingALoan).setScale(2, RoundingMode.HALF_UP);
        log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Total loan amount " + totalAmount);

        BigDecimal monthlyPayment = totalAmount.divide(new BigDecimal(loanApplicationRequestDTO.getTerm())
                .setScale(2, RoundingMode.HALF_UP), 3, RoundingMode.HALF_UP);
        log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Monthly payment " + monthlyPayment);

        return new LoanOfferDTO(applicationId, loanApplicationRequestDTO.getAmount(),
                totalAmount, loanApplicationRequestDTO.getTerm(),
                monthlyPayment, new BigDecimal(rate),
                isInsuranceEnabled, isSalaryClient);
    }

    public Double calculateRate(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
                                MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
                                Gender gender, int currentWorkingExperience, int totalWorkingExperience,
                                boolean isInsuranceEnabled, boolean isSalaryClient, BigDecimal loanAmount) {

        Double currentRate = conveyorConfigurationConstants.BASE_RATE;

        switch (employmentStatus) {
            case WORKING:
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_WORKING_ON_THE_RATE;
                log.info("Rate increased by 0.5 - worker");
                break;
            case ENTREPRENEUR:
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_ENTREPRENEUR_ON_THE_RATE;
                log.info("Rate increased by 3 - entrepreneur");
                break;
            case SELF_EMPLOYED:
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_SELF_EMPLOYED_ON_THE_RATE;
                log.info("Rate increased by 1 - self employed");
                break;
            case JOBLESS:
            default:
                log.warn("Denial of a loan - jobless ");
                return null;

        }
        switch (jobPosition) {
            case JUNIOR:
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_JUNIOR_ON_THE_RATE;
                log.info("Rate increased by 1 - junior");
                break;
            case MIDDLE:
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_MIDDLE_ON_THE_RATE;
                log.info("Rate decreased by 1 - middle");
                break;
            case SENIOR:
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_SENIOR_ON_THE_RATE;
                log.info("Rate decreased by 2 - senior");
                break;
            default:
                log.warn("Denial of a loan - incorrect work position");
                return null;
        }

        if (salary.multiply(BigDecimal.valueOf(conveyorConfigurationConstants.MULTIPLIER_MAX_NUMBER_OF_SALARIES_COMPARISON_WITH_REQUESTED_AMOUNT))
                .compareTo(loanAmount) < 0) {
            log.warn("Denial of a loan - loan > 20 x salaries");
            return null;
        }
        switch (martialStatus) {
            case MARRIED:
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_MARRIED_ON_THE_RATE;
                log.info("Rate decreased by 3 - married");
                break;
            case SINGLE:
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_SINGLE_ON_THE_RATE;
                log.info("Rate increased by 1 - single");
                break;
            case DIVORCE:
                log.info("Rate increased by 3 - divorce");
                currentRate += conveyorConfigurationConstants.INFLUENCE_OF_STATUS_DIVORCE_ON_THE_RATE;
                break;
            default:
                log.warn("Denial of a loan - incorrect marital status");
                return null;
        }
        if (dependentAmount > conveyorConfigurationConstants.MIN_COUNT_DEPENDENT_AMOUNT_WILL_INFLUENCE_RATE) {
            currentRate += conveyorConfigurationConstants.INFLUENCE_OF_DEPENDENT_AMOUNT_ON_THE_RATE;
            log.info("Rate increased by 1 - dependent amount > 1");
        }
        if (Period.between(birthDate, LocalDate.now()).getYears() < conveyorConfigurationConstants.MIN_AGE ||
                Period.between(birthDate, LocalDate.now()).getYears() > conveyorConfigurationConstants.MAX_AGE) {
            log.warn("Denial of a loan - <18 or >60 y.o.>");
            return null;
        }
        if (gender == Gender.MALE
                && (Period.between(birthDate, LocalDate.now()).getYears() > conveyorConfigurationConstants.MIN_MALE_RELIABLE_AGE
                || Period.between(birthDate, LocalDate.now()).getYears() < conveyorConfigurationConstants.MAX_MALE_RELIABLE_AGE)) {
            currentRate += conveyorConfigurationConstants.INFLUENCE_RELIABLE_AGE_ON_RATE;
            log.info("Rate decreased by 3 - male >30 <55 y.o");
        }
        if (gender == Gender.FEMALE
                && (Period.between(birthDate, LocalDate.now()).getYears() > conveyorConfigurationConstants.MAX_FEMALE_RELIABLE_AGE
                || Period.between(birthDate, LocalDate.now()).getYears() < conveyorConfigurationConstants.MAX_FEMALE_RELIABLE_AGE)) {
            currentRate += conveyorConfigurationConstants.INFLUENCE_RELIABLE_AGE_ON_RATE;
            log.info("Rate decreased by 3 - female >35 <60 y.o");
        }
        if (gender == Gender.OTHER) {
            log.info("Rate increased by 3 - just because non binary gender");
            currentRate += conveyorConfigurationConstants.INFLUENCE_OF_GENDER_OTHER_ON_RATE;
        }
        if (currentWorkingExperience < conveyorConfigurationConstants.MIN_CURRENT_WORK_EXPERIENCE
                || totalWorkingExperience < conveyorConfigurationConstants.MIN_TOTAL_WORK_EXPERIENCE) {
            log.warn("Denial of a loan - working exp current <3y or working exp total <12");
            return null;
        }
        if (isInsuranceEnabled) {
            currentRate += conveyorConfigurationConstants.INFLUENCE_OF_INSURANCE_ON_THE_RATE;
            log.info("Rate decreased by 3 - insurance enabled");
        }
        if (isSalaryClient) {
            log.info("Rate decreased by 1 - is salary client");
            currentRate += conveyorConfigurationConstants.INFLUENCE_OF_SALARY_CLIENT_ON_THE_RATE;
        }
        log.info("rate after scoring is " + currentRate);
        return currentRate;
    }

    public CreditDTO calculateCreditParams(double rate, BigDecimal amount,
                                           int term, boolean isInsuranceEnabled, boolean isSalaryClient) {

        List<BigDecimal> tempPaymentSchedule = new ArrayList<>();
        List<Integer> listQk = new ArrayList<>();
        List<Double> listEk = new ArrayList<>();
        List<LocalDate> dateOfPayment = new ArrayList<>();
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        double temDaysFromCreditToPayment;

        tempPaymentSchedule.add(0, amount.multiply(BigDecimal.valueOf(-1)));
        dateOfPayment.add(0, LocalDate.now());
        BigDecimal tempMonthlyPayment = amount.multiply(BigDecimal.valueOf((rate / (100 * 12))
                / (1 - Math.pow((1 + (rate / (100 * 12))), -term))));
        BigDecimal totalLoanCostWithInterestsPayments = tempMonthlyPayment.multiply(BigDecimal.valueOf(term));
        log.info("totalLoanCostWithInterestsPayments ------------>" + totalLoanCostWithInterestsPayments);

        for (int i = 0; i <= term; i++) {
            if (i != 0) {
                tempPaymentSchedule.add(i, tempMonthlyPayment);
                dateOfPayment.add(i, dateOfPayment.get(i - 1).plusMonths(1));
            }
            if (i == 0) {
                temDaysFromCreditToPayment = 0;
            } else {
                temDaysFromCreditToPayment = ChronoUnit.DAYS.between(dateOfPayment.get(0), dateOfPayment.get(i));
            }
            listEk.add((temDaysFromCreditToPayment % 30) / 30);
            listQk.add((int) Math.floor(temDaysFromCreditToPayment / 30));
        }

        BigDecimal i = BigDecimal.valueOf(0);
        BigDecimal x = BigDecimal.valueOf(0);
        BigDecimal i_max = BigDecimal.valueOf(rate * 2);
        BigDecimal i_temp;
        BigDecimal s = BigDecimal.valueOf(0.0000001);
        int count = 0;


        while (i.subtract(i_max).abs().compareTo(s) > 0) {
            x = BigDecimal.valueOf(0);
            count++;
            for (int k = 0; k <= term; k++) {
                // (1 + ek*i) ---> divisor1
                BigDecimal divisor1 = i.multiply(BigDecimal.valueOf(listEk.get(k))).add(BigDecimal.valueOf(1.0));
                // (1 + i)^qk ---> divisor2
                BigDecimal divisor2 = (i.add(BigDecimal.valueOf(1))).pow(listQk.get(k));
                //divisor1 * divisor2
                BigDecimal divisor1MultiplyDivisor2 = divisor1.multiply(divisor2).setScale(6, RoundingMode.HALF_UP);
                x = x.add(tempPaymentSchedule.get(k).divide(divisor1MultiplyDivisor2, 6, RoundingMode.HALF_UP));
            }

            if (x.compareTo(BigDecimal.valueOf(0)) < 0) {
                i_temp = i;
                i = i.subtract(i.subtract(i_max).abs().divide(BigDecimal.valueOf(2), 7, RoundingMode.HALF_UP));
                i_max = i_temp;
            } else {
                i = i.add(i.subtract(i_max).abs().divide(BigDecimal.valueOf(2), 7, RoundingMode.HALF_UP));
            }
        }
        log.info("takes " + count + " iterations to calculate i");


        BigDecimal psk = i.multiply(BigDecimal.valueOf((double) 365 / 30 * 100)).setScale(6, RoundingMode.HALF_UP);

        for (int n = 1; n <= term; n++) {
            BigDecimal totalPayment = tempMonthlyPayment.multiply(BigDecimal.valueOf(n));
            BigDecimal remainingDebt = totalLoanCostWithInterestsPayments.subtract(totalPayment);
            BigDecimal interestPayment = remainingDebt.multiply(i);
            BigDecimal debtPayment = tempMonthlyPayment.subtract(interestPayment);

            paymentSchedule.add(new PaymentScheduleElement(n, dateOfPayment.get(n),
                    totalPayment, interestPayment, debtPayment, remainingDebt));
        }

        log.info("Total loan amount with interest " + totalLoanCostWithInterestsPayments + ", monthly payment " + tempMonthlyPayment + ", psk " + psk +
                ", rate " + BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP) +
                ", insurance " + isInsuranceEnabled + ", is salary client " + isSalaryClient);

        return new CreditDTO(amount, term, tempMonthlyPayment, BigDecimal.valueOf(rate),
                psk, isInsuranceEnabled, isSalaryClient, paymentSchedule);
    }
}
