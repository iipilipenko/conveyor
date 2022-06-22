package com.pilipenko.deal.repository;
import com.pilipenko.deal.model.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<Credit, Long> {
}
