package com.pilipenko.creditconveyor.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:creditConveyor.properties")
public class ConveyorConfigurationConstants {

    @Value("${base.rate}")
    Double BASE_RATE;

    @Value("${influence.of.salary.client.on.the.rate}")
    Double INFLUENCE_OF_SALARY_CLIENT_ON_THE_RATE;

    @Value("${influence.of.insurance.on.the.rate}")
    Double INFLUENCE_OF_INSURANCE_ON_THE_RATE;

    @Value("${multiplier.for.calculating.insurance.cost}")
    Double MULTIPLIER_FOR_CALCULATING_INSURANCE_COST;

    @Value("${influence.of.status.working.on.the.rate}")
    Double INFLUENCE_OF_STATUS_WORKING_ON_THE_RATE;

    @Value("${min.age}")
    Double MIN_AGE;

    @Value("${max.age}")
    Double MAX_AGE;

    @Value("${influence.of.status.entrepreneur.on.the.rate}")
    Double INFLUENCE_OF_STATUS_ENTREPRENEUR_ON_THE_RATE;

    @Value("${influence.of.status.self.employed.on.the.rate}")
    Double INFLUENCE_OF_STATUS_SELF_EMPLOYED_ON_THE_RATE;

    @Value("${influence.of.status.junior.on.the.rate}")
    Double INFLUENCE_OF_STATUS_JUNIOR_ON_THE_RATE;

    @Value("${influence.of.status.middle.on.the.rate}")
    Double INFLUENCE_OF_STATUS_MIDDLE_ON_THE_RATE;

    @Value("${influence.of.status.senior.on.the.rate}")
    Double INFLUENCE_OF_STATUS_SENIOR_ON_THE_RATE;

    @Value("${influence.of.status.married.on.the.rate}")
    Double INFLUENCE_OF_STATUS_MARRIED_ON_THE_RATE;

    @Value("${influence.of.status.single.on.the.rate}")
    Double INFLUENCE_OF_STATUS_SINGLE_ON_THE_RATE;

    @Value("${influence.of.status.divorce.on.the.rate}")
    Double INFLUENCE_OF_STATUS_DIVORCE_ON_THE_RATE;

    @Value("${influence.of.dependent.amount.on.the.rate}")
    Double INFLUENCE_OF_DEPENDENT_AMOUNT_ON_THE_RATE;

    @Value("${multiplier.max.number.of.salaries.comparison.with.requested.amount}")
    Double MULTIPLIER_MAX_NUMBER_OF_SALARIES_COMPARISON_WITH_REQUESTED_AMOUNT;

    @Value("${min.male.reliable.age}")
    Double MIN_MALE_RELIABLE_AGE;

    @Value("${max.male.reliable.age}")
    Double MAX_MALE_RELIABLE_AGE;

    @Value("${min.female.reliable.age}")
    Double MIN_FEMALE_RELIABLE_AGE;

    @Value("${max.female.reliable.age}")
    Double MAX_FEMALE_RELIABLE_AGE;

    @Value("${influence.reliable.age.on.rate}")
    Double INFLUENCE_RELIABLE_AGE_ON_RATE;

    @Value("${influence.of.gender.other.on.rate}")
    Double INFLUENCE_OF_GENDER_OTHER_ON_RATE;

    @Value("${min.count.dependent.amount.will.influence.rate}")
    Double MIN_COUNT_DEPENDENT_AMOUNT_WILL_INFLUENCE_RATE;

    @Value("${min.current.work.experience}")
    Double MIN_CURRENT_WORK_EXPERIENCE;

    @Value("${min.total.work.experience}")
    Double MIN_TOTAL_WORK_EXPERIENCE;
}
