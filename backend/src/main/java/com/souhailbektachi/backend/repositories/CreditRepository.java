package com.souhailbektachi.backend.repositories;

import com.souhailbektachi.backend.entities.Credit;
import com.souhailbektachi.backend.entities.StatutCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByClientId(Long clientId);
    List<Credit> findByStatut(StatutCredit statut);
}
