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
public class ConveyorConfigurationMethods {

    @Autowired
    private ConveyorConfigurationParams conveyorConfigurationParams;

    LoanOfferDTO createLoanOfferDTO(boolean isInsuranceEnabled,
                                    boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO,
                                    Long applicationId) {

        log.info(applicationId + ": Pre scoring loan offer insurance: " + isInsuranceEnabled + " salary client: " + isSalaryClient);

        Double rate = conveyorConfigurationParams.getBaseRate();
        BigDecimal additionalServicesAmount;
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount();

        if (isSalaryClient) {
            rate += conveyorConfigurationParams.getInfluenceOfSalaryClientOnTheRate();
            log.info(applicationId + ": Rate decreased by 1 - salary client");
        }

        if (isInsuranceEnabled) {
            rate += conveyorConfigurationParams.getInfluenceOfInsuranceOnTheRate();
            log.info(applicationId + ": Rate decreased by 3 - insurance enabled");
            additionalServicesAmount = totalAmount.multiply(BigDecimal.valueOf(conveyorConfigurationParams.getMultiplierForCalculatingInsuranceCost()))
                    .setScale(2, RoundingMode.HALF_UP);
            log.info(applicationId + ": Insurance coast " + additionalServicesAmount);
            totalAmount = totalAmount.add(additionalServicesAmount).setScale(2, RoundingMode.HALF_UP);
        }
        // formula -> costOfUsingLoan = totalAmount * ( rate% / (12month * 100))
        BigDecimal costOfUsingALoan = totalAmount.multiply(
                new BigDecimal(rate / 1200 * loanApplicationRequestDTO.getTerm())).setScale(2, RoundingMode.HALF_UP);
        log.info(applicationId + ": Cost of using loan " + costOfUsingALoan);

        totalAmount = totalAmount.add(costOfUsingALoan).setScale(2, RoundingMode.HALF_UP);
        log.info(applicationId + ": Total loan amount " + totalAmount);

        BigDecimal monthlyPayment = totalAmount.divide(new BigDecimal(loanApplicationRequestDTO.getTerm())
                .setScale(2, RoundingMode.HALF_UP), 3, RoundingMode.HALF_UP);
        log.info(applicationId + ": Monthly payment " + monthlyPayment);

        log.info("Creating loan offers for user: id" + applicationId + " " +
                "name :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 1 loan offer is created with : rate " + rate +
                ", amount " + totalAmount.setScale(2, RoundingMode.HALF_UP) + ", term " + loanApplicationRequestDTO.getTerm() + ", monthly payment " +
                monthlyPayment.setScale(2, RoundingMode.HALF_UP));

        return new LoanOfferDTO(applicationId, loanApplicationRequestDTO.getAmount(),
                totalAmount, loanApplicationRequestDTO.getTerm(),
                monthlyPayment, new BigDecimal(rate),
                isInsuranceEnabled, isSalaryClient);
    }

    public Double calculateRate(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
                                MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
                                Gender gender, int currentWorkingExperience, int totalWorkingExperience,
                                boolean isInsuranceEnabled, boolean isSalaryClient, BigDecimal loanAmount, Long requestId) {

        Double currentRate = conveyorConfigurationParams.getBaseRate();

        log.info(requestId + "Base rate is: " + currentRate);

        switch (employmentStatus) {
            case WORKING:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusWorkingOnTheRate();
                log.info(requestId + "Rate increased by 0.5 - worker");
                break;
            case ENTREPRENEUR:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusEntrepreneurOnTheRate();
                log.info(requestId + "Rate increased by 3 - entrepreneur");
                break;
            case SELF_EMPLOYED:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusSelfEmployedOnTheRate();
                log.info(requestId + "Rate increased by 1 - self employed");
                break;
            case JOBLESS:
            default:
                log.warn("Denial of a loan - jobless ");
                return null;

        }
        switch (jobPosition) {
            case JUNIOR:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusJuniorOnTheRate();
                log.info(requestId + "Rate increased by 1 - junior");
                break;
            case MIDDLE:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusMiddleOnTheRate();
                log.info(requestId + "Rate decreased by 1 - middle");
                break;
            case SENIOR:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusSeniorOnTheRate();
                log.info(requestId + "Rate decreased by 2 - senior");
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
                log.info(requestId + "Rate decreased by 3 - married");
                break;
            case SINGLE:
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusSingleOnTheRate();
                log.info(requestId + "Rate increased by 1 - single");
                break;
            case DIVORCE:
                log.info(requestId + "Rate increased by 3 - divorce");
                currentRate += conveyorConfigurationParams.getInfluenceOfStatusDivorceOnTheRate();
                break;
            default:
                log.warn("Denial of a loan - incorrect marital status");
                return null;
        }
        if (dependentAmount > conveyorConfigurationParams.getMinCountDependentAmountWillInfluenceRate()) {
            currentRate += conveyorConfigurationParams.getInfluenceOfDependentAmountOnTheRate();
            log.info(requestId + "Rate increased by 1 - dependent amount > 1");
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
            log.info(requestId + "Rate decreased by 3 - male >30 <55 y.o");
        }
        if (gender == Gender.FEMALE
                && (Period.between(birthDate, LocalDate.now()).getYears() > conveyorConfigurationParams.getMaxFemaleReliableAge()
                || Period.between(birthDate, LocalDate.now()).getYears() < conveyorConfigurationParams.getMaxFemaleReliableAge())) {
            currentRate += conveyorConfigurationParams.getInfluenceReliableAgeOnRate();
            log.info(requestId + "Rate decreased by 3 - female >35 <60 y.o");
        }
        if (gender == Gender.OTHER) {
            log.info(requestId + "Rate increased by 3 - just because non binary gender");
            currentRate += conveyorConfigurationParams.getInfluenceOfGenderOtherOnRate();
        }
        if (currentWorkingExperience < conveyorConfigurationParams.getMinCurrentWorkExperience()
                || totalWorkingExperience < conveyorConfigurationParams.getMinTotalWorkExperience()) {
            log.warn("Denial of a loan - working exp current <3y or working exp total <12");
            return null;
        }
        if (isInsuranceEnabled) {
            currentRate += conveyorConfigurationParams.getInfluenceOfInsuranceOnTheRate();
            log.info(requestId + "Rate decreased by 3 - insurance enabled");
        }
        if (isSalaryClient) {
            log.info(requestId + "Rate decreased by 1 - is salary client");
            currentRate += conveyorConfigurationParams.getInfluenceOfSalaryClientOnTheRate();
        }
        log.info(requestId + "rate after scoring is " + currentRate);
        return currentRate;
    }

    public CreditDTO calculateCreditParams(double rate, BigDecimal amount,
                                           int term, boolean isInsuranceEnabled, boolean isSalaryClient, Long requestId) {

        log.info(requestId + "Calculating credit params");

        List<BigDecimal> tempPaymentSchedule = new ArrayList<>();
        List<Integer> listQk = new ArrayList<>();
        List<Double> listEk = new ArrayList<>();
        List<LocalDate> dateOfPayment = new ArrayList<>();
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        double temDaysFromCreditToPayment;

        tempPaymentSchedule.add(0, amount.multiply(BigDecimal.valueOf(-1)));
        dateOfPayment.add(0, LocalDate.now());
        //annuity payment value = amount * ( rateD / ( 1 - ( 1 + rateD)^-term))
        //where rateD = rate / (100*12), convert % -> decimal number
        BigDecimal tempMonthlyPayment = amount.multiply(BigDecimal.valueOf((rate / (100 * 12))
                / (1 - Math.pow((1 + (rate / (100 * 12))), -term))));
        BigDecimal totalLoanCostWithInterestsPayments = tempMonthlyPayment.multiply(BigDecimal.valueOf(term));
        log.info(requestId + "totalLoanCostWithInterestsPayments ------------>" + totalLoanCostWithInterestsPayments);

        for (int i = 0; i <= term; i++) {
            if (i != 0) {
                //add monthly payment and date of payment into array lists
                tempPaymentSchedule.add(i, tempMonthlyPayment);
                dateOfPayment.add(i, dateOfPayment.get(i - 1).plusMonths(1));
            }
            if (i == 0) {
                //days from credit to payment is 0 because this is the day we took out the loan
                temDaysFromCreditToPayment = 0;
            } else {
                temDaysFromCreditToPayment = ChronoUnit.DAYS.between(dateOfPayment.get(0), dateOfPayment.get(i));
            }
            // Ek = (daysFromCreditToPayment % daysInBasePeriod) / daysInBasePeriod
            // daysInBasePeriod = 365/12 = 30
            listEk.add((temDaysFromCreditToPayment % 30) / 30);

            // Qk = floor(daysFromCreditToPayment#K / daysInBasePeriod)
            // daysInBasePeriod = 365/12 = 30
            listQk.add((int) Math.floor(temDaysFromCreditToPayment / 30));
        }

        //start binary search from i=0
        BigDecimal i = BigDecimal.valueOf(0);
        //assume that we are good creditors and "i" is less than rate*2 because we don't have commissions or other
        // extra services that increases our PSK
        BigDecimal i_max = BigDecimal.valueOf(rate * 2);
        BigDecimal i_temp;
        BigDecimal s = BigDecimal.valueOf(conveyorConfigurationParams.getSearchAccuracyI());

        //counter for iterations for searching "i"
        int count = 0;

        while (i.subtract(i_max).abs().compareTo(s) > 0) {
            //in each iteration of searching "i" zeroing the value of "x"
            BigDecimal x = BigDecimal.valueOf(0);
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
        log.info(requestId + "takes " + count + " iterations to calculate i");

        // psk = i * CBP * 100
        // CBP = 365/30
        BigDecimal psk = i.multiply(BigDecimal.valueOf((double) 365 / 30 * 100)).setScale(6, RoundingMode.HALF_UP);

        for (int n = 1; n <= term; n++) {
            BigDecimal totalPayment = tempMonthlyPayment.multiply(BigDecimal.valueOf(n)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal remainingDebt = totalLoanCostWithInterestsPayments.subtract(totalPayment).setScale(2, RoundingMode.HALF_UP);
            BigDecimal interestPayment = remainingDebt.multiply(i).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = tempMonthlyPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);

            paymentSchedule.add(new PaymentScheduleElement(n, dateOfPayment.get(n),
                    totalPayment, interestPayment, debtPayment, remainingDebt));
        }

        log.info(requestId + "Total loan amount with interest " + totalLoanCostWithInterestsPayments + ", monthly payment "
                + tempMonthlyPayment + ", psk " + psk + ", rate " + BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP) +
                ", insurance " + isInsuranceEnabled + ", is salary client " + isSalaryClient);

        return new CreditDTO(amount, term, tempMonthlyPayment.setScale(2, BigDecimal.ROUND_HALF_UP), BigDecimal.valueOf(rate),
                psk, isInsuranceEnabled, isSalaryClient, paymentSchedule);
    }
}
