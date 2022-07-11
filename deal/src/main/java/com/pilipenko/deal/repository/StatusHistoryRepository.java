package com.pilipenko.deal.repository;

import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusHistoryRepository  extends JpaRepository<StatusHistory, Long> {
    public List<StatusHistory> findStatusHistoryByApplication (Application application);
}
