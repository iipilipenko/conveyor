package com.pilipenko.deal.repository;

import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.model.ClientID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    public List<Client> findByNumberAndSeries (Integer number, Integer series);
}
