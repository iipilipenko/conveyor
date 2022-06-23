package com.pilipenko.deal.repository;

import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.LoanOfferDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanOfferDTORepository extends JpaRepository<LoanOfferDTO, Long> {
}
