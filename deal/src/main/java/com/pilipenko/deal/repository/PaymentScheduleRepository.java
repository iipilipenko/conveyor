package com.pilipenko.deal.repository;

import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.model.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {
}
