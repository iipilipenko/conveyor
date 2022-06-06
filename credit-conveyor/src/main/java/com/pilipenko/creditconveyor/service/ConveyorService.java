package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.*;
import com.pilipenko.creditconveyor.enums.EmploymentStatus;
import com.pilipenko.creditconveyor.enums.Gender;
import com.pilipenko.creditconveyor.enums.JobPosition;
import com.pilipenko.creditconveyor.enums.MartialStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@PropertySource("classpath:creditConveyor.properties")
public class ConveyorService {

    @Value("${base.rate}")
    Double baseRate;

    public List<LoanOfferDTO> createLoanOffers(@Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Long applicationId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        ArrayList<LoanOfferDTO> loanOffers = new ArrayList<>();
        loanOffers.add(getLoanOfferDTO(true, true, loanApplicationRequestDTO, applicationId));
        loanOffers.add(getLoanOfferDTO(false, false, loanApplicationRequestDTO, applicationId));
        loanOffers.add(getLoanOfferDTO(true, false, loanApplicationRequestDTO, applicationId));
        loanOffers.add(getLoanOfferDTO(false, true, loanApplicationRequestDTO, applicationId));

        log.info("Creating loan offers for user: " + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() +
                "user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 1 loan offer is created with : rate " + loanOffers.get(0).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(0).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(0).getTerm() + ", monthly payment " +
                loanOffers.get(0).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP) + "\n" +
                "user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 2 loan offer is created with : rate " + loanOffers.get(1).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(1).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(1).getTerm() + ", monthly payment " +
                loanOffers.get(1).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP) + "\n" +
                "user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 3 loan offer is created with : rate " + loanOffers.get(2).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(2).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(2).getTerm() + ", monthly payment " +
                loanOffers.get(2).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP) + "\n" +
                "user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 4 loan offer is created with : rate " + loanOffers.get(3).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(3).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(3).getTerm() + ", monthly payment " +
                loanOffers.get(3).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));

        Comparator<LoanOfferDTO> rateComparator = Comparator.comparing(LoanOfferDTO::getRate);
        loanOffers.sort(Collections.reverseOrder(rateComparator));
        return loanOffers;
    }

    private LoanOfferDTO getLoanOfferDTO(boolean isInsuranceEnabled,
                                         boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO,
                                         Long applicationId) {

        log.info("Pre scoring loan offer insurance: " + isInsuranceEnabled + " salary client: " + isSalaryClient);

        Double rate = baseRate;
        BigDecimal additionalServicesAmount = new BigDecimal(0);
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount();

        if (isSalaryClient) {
            rate -= 1;
            log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Rate decreased by 1 - salary client");
        }

        if (isInsuranceEnabled) {
            rate -= 3;
            log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Rate decreased by 3 - insurance enabled");
            additionalServicesAmount = totalAmount.multiply(new BigDecimal(0.01)).setScale(2, RoundingMode.HALF_UP);
            log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Insurance coast " + additionalServicesAmount);
            totalAmount = totalAmount.add(additionalServicesAmount).setScale(2, RoundingMode.HALF_UP);

        }

        BigDecimal costOfUsingALoan = totalAmount.multiply(
                new BigDecimal(rate / 1200 * loanApplicationRequestDTO.getTerm())).setScale(2, RoundingMode.HALF_UP);
        log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Cost of using loan " + costOfUsingALoan);

        totalAmount = totalAmount.add(costOfUsingALoan).setScale(2, RoundingMode.HALF_UP);
        log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Total loan amount " + totalAmount);

        BigDecimal monthlyPayment = totalAmount.divide(new BigDecimal(loanApplicationRequestDTO.getTerm()).setScale(2, RoundingMode.HALF_UP), 3, RoundingMode.HALF_UP);
        log.info(loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " Monthly payment " + monthlyPayment);

        return new LoanOfferDTO(applicationId, loanApplicationRequestDTO.getAmount(),
                totalAmount, loanApplicationRequestDTO.getTerm(),
                monthlyPayment, new BigDecimal(rate),
                isInsuranceEnabled, isSalaryClient);
    }

    public CreditDTO createCreditDTO(@Valid ScoringDataDTO scoringDataDTO) {

        log.info("Scoring loan offer for user: " + scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName());

        Double rate = calculateRate(scoringDataDTO.getEmployment().getEmploymentStatus(), scoringDataDTO.getEmployment().getPosition(),
                scoringDataDTO.getEmployment().getSalary(), scoringDataDTO.getMaritalStatus(), scoringDataDTO.getDependentAmount(),
                scoringDataDTO.getBirthdate(), scoringDataDTO.getGender(), scoringDataDTO.getEmployment().getWorkExperienceCurrent(),
                scoringDataDTO.getEmployment().getWorkExperienceTotal(), scoringDataDTO.getIsInsuranceEnabled(),
                scoringDataDTO.getIsSalaryClient(), scoringDataDTO.getAmount());

        if (rate == null) {
            return null;
        }

        log.info("Calculating credit params");

        return calculateCreditParams(rate, scoringDataDTO.getAmount(), scoringDataDTO.getTerm(),
                scoringDataDTO.getIsInsuranceEnabled(), scoringDataDTO.getIsSalaryClient());

    }

    public Double calculateRate(EmploymentStatus employmentStatus, JobPosition jobPosition, BigDecimal salary,
                                MartialStatus martialStatus, int dependentAmount, LocalDate birthDate,
                                Gender gender, int currentWorkingExperience, int totalWorkingExperience,
                                boolean isInsuranceEnabled, boolean isSalaryClient, BigDecimal loanAmount) {

        Double currentRate = baseRate;

        switch (employmentStatus) {
            case WORKING:
                currentRate += 0.5;
                log.info("Rate increased by 0.5 - worker");
                break;
            case ENTREPRENEUR:
                currentRate += 3;
                log.info("Rate increased by 3 - entrepreneur");
                break;
            case SELF_EMPLOYED:
                currentRate += 1;
                log.info("Rate increased by 1 - self employed");
                break;
            case JOBLESS:
            default:
                log.warn("Denial of a loan - jobless ");
                return null;

        }
        switch (jobPosition) {
            case JUNIOR:
                currentRate += 1;
                log.info("Rate increased by 1 - junior");
                break;
            case MIDDLE:
                currentRate -= 1;
                log.info("Rate decreased by 1 - middle");
                break;
            case SENIOR:
                currentRate -= 2;
                log.info("Rate decreased by 2 - senior");
                break;
            default:
                log.warn("Denial of a loan - incorrect work position");
                return null;
        }
        if (salary.multiply(BigDecimal.valueOf(20)).compareTo(loanAmount) < 0) {
            log.warn("Denial of a loan - loan > 20 x salaries");
            return null;
        }
        switch (martialStatus) {
            case MARRIED:
                currentRate -= 3;
                log.info("Rate decreased by 3 - married");
                break;
            case SINGLE:
                currentRate += 1;
                log.info("Rate increased by 1 - single");
                break;
            case DIVORCE:
                log.info("Rate increased by 3 - divorce");
                currentRate += 3;
                break;
            default:
                log.warn("Denial of a loan - incorrect marital status");
                return null;
        }
        if (dependentAmount > 1) {
            currentRate += 1;
            log.info("Rate increased by 1 - dependent amount > 1");
        }
        if (Period.between(birthDate, LocalDate.now()).getYears() < 18 ||
                Period.between(birthDate, LocalDate.now()).getYears() > 60) {
            log.warn("Denial of a loan - <18 or >60 y.o.>");
            return null;
        }
        if (gender == Gender.MALE
                && (Period.between(birthDate, LocalDate.now()).getYears() > 30
                || Period.between(birthDate, LocalDate.now()).getYears() < 55)) {
            currentRate -= 3;
            log.info("Rate decreased by 3 - male >30 <55 y.o");
        }
        if (gender == Gender.FEMALE
                && (Period.between(birthDate, LocalDate.now()).getYears() > 35
                || Period.between(birthDate, LocalDate.now()).getYears() < 60)) {
            currentRate -= 3;
            log.info("Rate decreased by 3 - female >35 <60 y.o");
        }
        if (gender == Gender.OTHER) {
            log.info("Rate increased by 3 - just because non binary gender");
            currentRate += 3;
        }
        if (currentWorkingExperience < 3
                || totalWorkingExperience < 12) {
            log.warn("Denial of a loan - working exp current <3y or working exp total <12");
            return null;
        }
        if (isInsuranceEnabled) {
            currentRate -= 3;
            log.info("Rate decreased by 3 - insurance enabled");
        }
        if (isSalaryClient) {
            log.info("Rate decreased by 1 - is salary client");
            currentRate -= 1;
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

        BigDecimal i = BigDecimal.valueOf(0.011875);
        BigDecimal x = BigDecimal.valueOf(1.0);
        BigDecimal x_m = BigDecimal.valueOf(0.0);
        BigDecimal s = BigDecimal.valueOf(0.000001);

        while (x.compareTo(BigDecimal.valueOf(0)) > 0) {
            x_m = x;
            x = BigDecimal.valueOf(0);
            for (int k = 0; k <= term; k++) {

                // (1 + ek*i) ---> divisor1
                BigDecimal divisor1 = i.multiply(BigDecimal.valueOf(listEk.get(k))).add(BigDecimal.valueOf(1.0));

                // (1 + i)^qk ---> divisor2
                BigDecimal divisor2 = (i.add(BigDecimal.valueOf(1))).pow(listQk.get(k));

                //divisor1 * divisor2
                BigDecimal divisor1MultiplyDivisor2 = divisor1.multiply(divisor2).setScale(6, RoundingMode.HALF_UP);

                x = x.add(tempPaymentSchedule.get(k).divide(divisor1MultiplyDivisor2, 6, RoundingMode.HALF_UP));

            }
            i = i.add(s);
        }
        if (x.compareTo(x_m) > 0) {
            i = i.subtract(s);
        }

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
