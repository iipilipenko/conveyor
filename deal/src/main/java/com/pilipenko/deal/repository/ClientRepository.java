package com.pilipenko.deal.repository;

import com.pilipenko.deal.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    public List<Client> findByNumberAndSeries (String number, String series);
}
