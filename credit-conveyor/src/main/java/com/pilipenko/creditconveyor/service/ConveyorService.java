package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.*;
import com.pilipenko.creditconveyor.enums.Gender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
        ArrayList<LoanOfferDTO> loanOffers = new ArrayList<>();
        log.info("Creating loan offers for user: " + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName());
        loanOffers.add(getLoanOfferDTO(true, true, loanApplicationRequestDTO));
        log.info("user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 1 loan offer is created with : rate " + loanOffers.get(0).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(0).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(0).getTerm() + ", monthly payment " +
                loanOffers.get(0).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));
        loanOffers.add(getLoanOfferDTO(false, false, loanApplicationRequestDTO));
        log.info("user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 2 loan offer is created with : rate " + loanOffers.get(1).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(1).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(1).getTerm() + ", monthly payment " +
                loanOffers.get(1).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));
        loanOffers.add(getLoanOfferDTO(true, false, loanApplicationRequestDTO));
        log.info("user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 3 loan offer is created with : rate " + loanOffers.get(2).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(2).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(2).getTerm() + ", monthly payment " +
                loanOffers.get(2).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));
        loanOffers.add(getLoanOfferDTO(false, true, loanApplicationRequestDTO));
        log.info("user :" + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getLastName() + " 4 loan offer is created with : rate " + loanOffers.get(3).getRate().setScale(2, RoundingMode.HALF_UP) +
                ", amount " + loanOffers.get(3).getTotalAmount().setScale(2, RoundingMode.HALF_UP) + ", term " + loanOffers.get(3).getTerm() + ", monthly payment " +
                loanOffers.get(3).getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));
        Comparator<LoanOfferDTO> rateComparator = Comparator.comparing(LoanOfferDTO::getRate);
        loanOffers.sort(Collections.reverseOrder(rateComparator));
        return loanOffers;
    }

    private LoanOfferDTO getLoanOfferDTO(boolean isInsuranceEnabled,
                                         boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO) {

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

        Long applicationId = 1l;

        return new LoanOfferDTO(applicationId, loanApplicationRequestDTO.getAmount(),
                totalAmount, loanApplicationRequestDTO.getTerm(),
                monthlyPayment, new BigDecimal(rate),
                isInsuranceEnabled, isSalaryClient);
    }

    public CreditDTO createCreditDTO(@Valid ScoringDataDTO scoringDataDTO) {

        log.info("Scoring loan offer for user: " + scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName());

        Double rate = baseRate;
        BigDecimal value20Salaries = scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20));
        LocalDate birthDate = scoringDataDTO.getBirthdate();
        BigDecimal insurancePrice;

        switch (scoringDataDTO.getEmployment().getEmploymentStatus()) {
            case WORKING:
                baseRate += 0.5;
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate increased by 0.5 - worker");
                break;
            case ENTREPRENEUR:
                baseRate += 3;
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate increased by 3 - entrepreneur");
                break;
            case SELF_EMPLOYED:
                baseRate += 1;
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate increased by 1 - self employed");
                break;
            case JOBLESS:
            default:
                log.warn(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Denial of a loan - jobless ");
                return null;

        }
        switch (scoringDataDTO.getEmployment().getPosition()) {
            case JUNIOR:
                baseRate += 1;
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate increased by 1 - junior");
                break;
            case MIDDLE:
                baseRate -= 1;
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate decreased by 1 - middle");
                break;
            case SENIOR:
                baseRate -= 2;
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate decreased by 2 - senior");
                break;
            default:
                log.warn(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Denial of a loan - incorrect work position");
                return null;
        }
        if (scoringDataDTO.getAmount().compareTo(value20Salaries) > 0) {
            log.warn(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Denial of a loan - loan > 20 x salaries");
            return null;
        }
        switch (scoringDataDTO.getMaritalStatus()) {
            case MARRIED:
                baseRate -= 3;
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate decreased by 3 - married");
                break;
            case SINGLE:
                baseRate += 1;
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate increased by 1 - single");
                break;
            case DIVORCE:
                log.info(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Rate increased by 3 - divorce");
                baseRate += 3;
                break;
            default:
                log.warn(scoringDataDTO.getFirstName() + " " + scoringDataDTO.getLastName() + " Denial of a loan - incorrect marital status");
                return null;
        }
        if (scoringDataDTO.getDependentAmount() > 1) {
            baseRate += 1;
        }
        if (Period.between(birthDate, LocalDate.now()).getYears() < 18 ||
                Period.between(birthDate, LocalDate.now()).getYears() > 60) {
            System.out.println("my age " + Period.between(birthDate, LocalDate.now()).getYears());
            return null;
        }
        if (scoringDataDTO.getGender() == Gender.MALE
                && Period.between(LocalDate.now(), birthDate).getYears() > 30
                && Period.between(LocalDate.now(), birthDate).getYears() < 55) {
            baseRate -= 3;
        }
        if (scoringDataDTO.getGender() == Gender.FEMALE
                && Period.between(LocalDate.now(), birthDate).getYears() > 35
                && Period.between(LocalDate.now(), birthDate).getYears() < 60) {
            baseRate -= 3;
        }
        if (scoringDataDTO.getGender() == Gender.OTHER) {
            baseRate += 3;
        }
        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3
                || scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            System.out.println("im in working exp");
            return null;
        }
        if (scoringDataDTO.getIsInsuranceEnabled()) {
            baseRate -= 3;
        }
        if (scoringDataDTO.getIsSalaryClient()) {
            baseRate -= 1;
        }

        return calculateCreditParams(baseRate, scoringDataDTO.getAmount(), scoringDataDTO.getTerm(),
                scoringDataDTO.getIsInsuranceEnabled(), scoringDataDTO.getIsSalaryClient());

    }

    private CreditDTO calculateCreditParams(double rate, BigDecimal amount,
                                            int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        List<BigDecimal> tempPaymentSchedule = new ArrayList<>();
        List<Integer> daysFromGetCreditToKPayment = new ArrayList<>();
        List<Double> listQk = new ArrayList<>();
        List<Double> listEk = new ArrayList<>();
        List<LocalDate> dateOfPayment = new ArrayList<>();
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        double temDaysFromCreditToPayment;
        final int CBP = 12;

        tempPaymentSchedule.add(0, amount.multiply(BigDecimal.valueOf(-1)));
        dateOfPayment.add(0, LocalDate.now());
        BigDecimal tempMonthlyPayment = amount.multiply(BigDecimal.valueOf((rate / (100 * 12))
                / (1 - Math.pow((1 + (rate / (100 * 12))), -term))));

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
            daysFromGetCreditToKPayment.add((int) temDaysFromCreditToPayment);
            listEk.add((temDaysFromCreditToPayment % 30) / 30);
            listQk.add(Math.floor(temDaysFromCreditToPayment / 30));
        }

        double i = 0;
        BigDecimal x = BigDecimal.valueOf(1.0);
        BigDecimal x_m = BigDecimal.valueOf(0.0);
        double s = 0.000001;
        while (x.compareTo(BigDecimal.valueOf(0)) > 0) {
            x_m = x;
            x = BigDecimal.valueOf(0);
            for (int k = 0; k <= term; k++) {
                BigDecimal tempX = x.add(tempPaymentSchedule.get(k).divide((BigDecimal.
                        valueOf(((1 + listEk.get(k) * i) * Math.pow((1 + i), listQk.get(k))))), 6, RoundingMode.FLOOR));
                x = tempX;
            }
            System.out.println(x);
            i = i + s;
        }
        if (x.compareTo(x_m) > 0) {
            i = i - s;
        }

        BigDecimal psk = BigDecimal.valueOf(Math.floor(i * 12 * 100 * 1000) / 1000);

        for (int n = 1; n <= term; n++) {
            BigDecimal totalPayment = tempMonthlyPayment.multiply(BigDecimal.valueOf(n));
            BigDecimal remainingDebt = amount.subtract(totalPayment);
            BigDecimal interestPayment = remainingDebt.multiply(BigDecimal.valueOf(i));
            BigDecimal debtPayment = tempMonthlyPayment.subtract(interestPayment);

            paymentSchedule.add(new PaymentScheduleElement(n, dateOfPayment.get(n),
                    totalPayment, interestPayment, debtPayment, remainingDebt));
        }

        return new CreditDTO(amount, term, tempMonthlyPayment, BigDecimal.valueOf(rate),
                psk, isInsuranceEnabled, isSalaryClient, paymentSchedule);


    }


}
