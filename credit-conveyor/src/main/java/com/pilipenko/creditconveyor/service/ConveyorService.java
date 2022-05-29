package com.pilipenko.creditconveyor.service;

import com.pilipenko.creditconveyor.dto.CreditDTO;
import com.pilipenko.creditconveyor.dto.LoanApplicationRequestDTO;
import com.pilipenko.creditconveyor.dto.LoanOfferDTO;
import com.pilipenko.creditconveyor.dto.ScoringDataDTO;
import com.pilipenko.creditconveyor.enums.Gender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
@PropertySource("classpath:creditConveyor.properties")
public class ConveyorService {

    @Value("${base.rate}")
    Double baseRate;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    public List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        ArrayList<LoanOfferDTO> loanOffers = new ArrayList<>();
        loanOffers.add(getLoanOfferDTO(true, true, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(false, false, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(true, false, loanApplicationRequestDTO));
        loanOffers.add(getLoanOfferDTO(false, true, loanApplicationRequestDTO));
        Comparator<LoanOfferDTO> rateComparator = Comparator.comparing(LoanOfferDTO::getRate);
        loanOffers.sort(Collections.reverseOrder(rateComparator));
        return loanOffers;
    }

    private LoanOfferDTO getLoanOfferDTO(boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO) {
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

        BigDecimal costOfUsingALoan = loanApplicationRequestDTO.getAmount().multiply(
                new BigDecimal(rate * loanApplicationRequestDTO.getTerm()));

        totalAmount = totalAmount.add(additionalServicesAmount);
        totalAmount = totalAmount.add(costOfUsingALoan);
        BigDecimal monthlyPayment = totalAmount.divide(new BigDecimal(loanApplicationRequestDTO.getTerm()));

        return new LoanOfferDTO(applicationId, loanApplicationRequestDTO.getAmount(),
                totalAmount, loanApplicationRequestDTO.getTerm(),
                monthlyPayment, new BigDecimal(rate),
                isInsuranceEnabled, isSalaryClient);
    }

    public CreditDTO createCreditDTO (ScoringDataDTO scoringDataDTO) {
        Double rate = baseRate;
        BigDecimal value20Salaries = scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20));
        LocalDate birthDate = scoringDataDTO.getBirthdate();
        BigDecimal insurancePrice;

        switch (scoringDataDTO.getEmployment().getEmploymentStatus()) {
            case WORKING:
                baseRate+=0.5;
                break;
            case ENTREPRENEUR:
                baseRate+=3;
                break;
            case SELF_EMPLOYED:
                baseRate+=1;
                break;
            case JOBLESS:
            default:
                return null;
        }
        switch (scoringDataDTO.getEmployment().getPosition()) {
            case JUNIOR:
                baseRate+=1;
                break;
            case MIDDLE:
                baseRate-=1;
                break;
            case SENIOR:
                baseRate-=2;
                break;
            default:
                return null;
        }
        if (scoringDataDTO.getAmount().compareTo(value20Salaries)<0) {
            return null;
        }
        switch (scoringDataDTO.getMaritalStatus()) {
            case MARRIED:
                baseRate-=3;
                break;
            case SINGLE:
                baseRate+=1;
                break;
            case DIVORCE:
                baseRate+=3;
                break;
            default:
                return null;
        }
        if (scoringDataDTO.getDependentAmount()>1) {
            baseRate+=1;
        }
        if (Period.between(LocalDate.now(), birthDate).getYears() < 18 ||
                Period.between(LocalDate.now(), birthDate).getYears() > 60) {
            return null;
        }
        if (scoringDataDTO.getGender() == Gender.MALE
                && Period.between(LocalDate.now(), birthDate).getYears() > 30
                && Period.between(LocalDate.now(), birthDate).getYears() < 55) {
            baseRate -=3;
        }
        if (scoringDataDTO.getGender() == Gender.FEMALE
                && Period.between(LocalDate.now(), birthDate).getYears() > 35
                && Period.between(LocalDate.now(), birthDate).getYears() < 60) {
            baseRate -=3;
        }
        if (scoringDataDTO.getGender() == Gender.OTHER) {
            baseRate +=3;
        }
        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3
                || scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            return null;
        }
        if (scoringDataDTO.getIsInsuranceEnabled()) {
            insurancePrice = scoringDataDTO.getAmount().multiply(BigDecimal.valueOf(0.01));
        }
        if (scoringDataDTO.getIsSalaryClient()) {
            baseRate-=1;
        }



    }

    private CreditDTO calculateCreditParams (double rate, BigDecimal amount, int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        List<BigDecimal> paymentSchedule = new ArrayList<>();
        List<Integer> daysFromGetCreditToKPayment = new ArrayList<>();
        List<Double> listQk = new ArrayList<>();
        List<Double> listEk = new ArrayList<>();
        final int CBP = 12;

            paymentSchedule.add(0, amount.multiply(BigDecimal.valueOf(-1)));

            BigDecimal tempMonthlyPayment = amount.multiply(BigDecimal.valueOf((rate / (100 * 12)) / (1 - Math.pow((1+ (rate / (100 * 12))), -term))));

            for (int i = 0; i <= term; i++) {
                if (i != 0) {
                    paymentSchedule.add(i, tempMonthlyPayment);
                }
                double temDaysFromCreditToPayment = i * 30;
                daysFromGetCreditToKPayment.add(i * 30);
                listEk.add((temDaysFromCreditToPayment % 30) / 30);
                listQk.add(Math.floor(temDaysFromCreditToPayment / 30));
            }


        public double getPsk() {
            int count = 0;
            double i = 0;
            double x = 1.0;
            double x_m = 0.0;
            double s = 0.000001;
            while (x > 0) {
                x_m = x;
                x = 0;
                for (int k = 0; k <= term; k++) {
                    x = x + paymentSchedule.get(k) / ( Math.pow((1 + i), this.listQk.get(k)));
                    if (k==12) {
                        System.out.println("k: " + k + "; i = "+ i + " result =" + x);
                    }
                }
                i = i + s;
                count++;
            }
            if (x > x_m) {
                i = i - s;
            }
            System.out.println("number try "+ count);
            return Math.floor(i * 12 * 100 * 1000) / 1000;


        }


    }


}
