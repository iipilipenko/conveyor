package com.pilipenko.deal.repository;
import com.pilipenko.deal.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    public Application findApplicationById (Long id);
}
