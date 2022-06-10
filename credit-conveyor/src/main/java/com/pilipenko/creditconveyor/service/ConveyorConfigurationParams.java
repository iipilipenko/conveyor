package com.pilipenko.creditconveyor.service;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:creditConveyor.properties")
public class ConveyorConfigurationParams {

    @Value("${base.rate}")
    @Getter private Double baseRate;

    @Value("${influence.of.salary.client.on.the.rate}")
    @Getter private Double influenceOfSalaryClientOnTheRate;

    @Value("${influence.of.insurance.on.the.rate}")
    @Getter private Double influenceOfInsuranceOnTheRate;

    @Value("${multiplier.for.calculating.insurance.cost}")
    @Getter private Double multiplierForCalculatingInsuranceCost;

    @Value("${influence.of.status.working.on.the.rate}")
    @Getter private Double influenceOfStatusWorkingOnTheRate;

    @Value("${min.age}")
    @Getter private Double minAge;

    @Value("${max.age}")
    @Getter private Double maxAge;

    @Value("${influence.of.status.entrepreneur.on.the.rate}")
    @Getter private Double influenceOfStatusEntrepreneurOnTheRate;

    @Value("${influence.of.status.self.employed.on.the.rate}")
    @Getter private Double influenceOfStatusSelfEmployedOnTheRate;

    @Value("${influence.of.status.junior.on.the.rate}")
    @Getter private Double influenceOfStatusJuniorOnTheRate;

    @Value("${influence.of.status.middle.on.the.rate}")
    @Getter private Double influenceOfStatusMiddleOnTheRate;

    @Value("${influence.of.status.senior.on.the.rate}")
    @Getter private Double influenceOfStatusSeniorOnTheRate;

    @Value("${influence.of.status.married.on.the.rate}")
    @Getter private Double influenceOfStatusMarriedOnTheRate;

    @Value("${influence.of.status.single.on.the.rate}")
    @Getter private Double influenceOfStatusSingleOnTheRate;

    @Value("${influence.of.status.divorce.on.the.rate}")
    @Getter private Double influenceOfStatusDivorceOnTheRate;

    @Value("${influence.of.dependent.amount.on.the.rate}")
    @Getter private Double influenceOfDependentAmountOnTheRate;

    @Value("${multiplier.max.number.of.salaries.comparison.with.requested.amount}")
    @Getter private Double multiplierMaxNumberOfSalariesComparisonWithRequestedAmount;

    @Value("${min.male.reliable.age}")
    @Getter private Double minMaleReliableAge;

    @Value("${max.male.reliable.age}")
    @Getter private Double maxMaleReliableAge;

    @Value("${min.female.reliable.age}")
    @Getter private Double minFemaleReliableAge;

    @Value("${max.female.reliable.age}")
    @Getter private Double maxFemaleReliableAge;

    @Value("${influence.reliable.age.on.rate}")
    @Getter private Double influenceReliableAgeOnRate;

    @Value("${influence.of.gender.other.on.rate}")
    @Getter private Double influenceOfGenderOtherOnRate;

    @Value("${min.count.dependent.amount.will.influence.rate}")
    @Getter private Double minCountDependentAmountWillInfluenceRate;

    @Value("${min.current.work.experience}")
    @Getter private Double minCurrentWorkExperience;

    @Value("${min.total.work.experience}")
    @Getter private Double minTotalWorkExperience;

    @Value("${search.accuracy.i}")
    @Getter private Double searchAccuracyI;
}
