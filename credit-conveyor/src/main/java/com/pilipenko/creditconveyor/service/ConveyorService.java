package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.*;
import com.pilipenko.creditconveyor.enums.Gender;
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


@Service
@PropertySource("classpath:creditConveyor.properties")
public class ConveyorService {

    @Value("${base.rate}")
    Double baseRate;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    public List<LoanOfferDTO> createLoanOffers(@Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {
        ArrayList<LoanOfferDTO> loanOffers = new ArrayList<>();
        loanOffers.add(getLoanOfferDTO(true, true, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(false, false, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(true, false, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(false, true, loanApplicationRequestDTO));
        Comparator<LoanOfferDTO> rateComparator = Comparator.comparing(LoanOfferDTO::getRate);
        loanOffers.sort(Collections.reverseOrder(rateComparator));
        return loanOffers;
    }

    private LoanOfferDTO getLoanOfferDTO(boolean isInsuranceEnabled,
                                         boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO) {

        Double rate = baseRate;
        BigDecimal additionalServicesAmount = new BigDecimal(0);
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount();

        if (isSalaryClient) {
            rate -= 1;
        }

        if (isInsuranceEnabled) {
            rate -= 3;
            additionalServicesAmount = totalAmount.multiply(new BigDecimal(0.01));
        }

        totalAmount = totalAmount.add(additionalServicesAmount);

        BigDecimal costOfUsingALoan = totalAmount.multiply(
                new BigDecimal(rate / 1200 * loanApplicationRequestDTO.getTerm()));

        totalAmount = totalAmount.add(costOfUsingALoan);
        BigDecimal monthlyPayment = totalAmount.divide(new BigDecimal(loanApplicationRequestDTO.getTerm()), 3, RoundingMode.HALF_UP);

        return new LoanOfferDTO(applicationId, loanApplicationRequestDTO.getAmount(),
                totalAmount, loanApplicationRequestDTO.getTerm(),
                monthlyPayment, new BigDecimal(rate),
                isInsuranceEnabled, isSalaryClient);
    }

    public CreditDTO createCreditDTO(@Valid ScoringDataDTO scoringDataDTO) {

        Double rate = baseRate;
        BigDecimal value20Salaries = scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20));
        LocalDate birthDate = scoringDataDTO.getBirthdate();
        BigDecimal insurancePrice;

        switch (scoringDataDTO.getEmployment().getEmploymentStatus()) {
            case WORKING:
                baseRate += 0.5;
                break;
            case ENTREPRENEUR:
                baseRate += 3;
                break;
            case SELF_EMPLOYED:
                baseRate += 1;
                break;
            case JOBLESS:
            default:
                System.out.println("im in work status");
                return null;

        }
        switch (scoringDataDTO.getEmployment().getPosition()) {
            case JUNIOR:
                baseRate += 1;
                break;
            case MIDDLE:
                baseRate -= 1;
                break;
            case SENIOR:
                baseRate -= 2;
                break;
            default:
                System.out.println("im in work position");
                return null;
        }
        if (scoringDataDTO.getAmount().compareTo(value20Salaries) > 0) {
            System.out.println("im in salary");
            return null;
        }
        switch (scoringDataDTO.getMaritalStatus()) {
            case MARRIED:
                baseRate -= 3;
                break;
            case SINGLE:
                baseRate += 1;
                break;
            case DIVORCE:
                baseRate += 3;
                break;
            default:
                System.out.println("im in marital status");
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
                dateOfPayment.add(i, dateOfPayment.get(i-1).plusMonths(1));
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
                BigDecimal tempX = x.add( tempPaymentSchedule.get(k).divide((BigDecimal.
                        valueOf( ((1+listEk.get(k)*i)*Math.pow((1 + i), listQk.get(k))))),6, RoundingMode.FLOOR));
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
