package com.souhailbektachi.backend.repositories;

import com.souhailbektachi.backend.entities.Credit;
import com.souhailbektachi.backend.entities.CreditImmobilier;
import com.souhailbektachi.backend.entities.CreditPersonnel;
import com.souhailbektachi.backend.entities.CreditProfessionnel;
import com.souhailbektachi.backend.entities.StatutCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByClientId(Long clientId);
    List<Credit> findByStatut(StatutCredit statut);
    
    @Query("SELECT c FROM CreditPersonnel c")
    List<Credit> findCreditPersonnel();
    
    @Query("SELECT c FROM CreditImmobilier c")
    List<Credit> findCreditImmobilier();
    
    @Query("SELECT c FROM CreditProfessionnel c")
    List<Credit> findCreditProfessionnel();
    
    List<Credit> findByMontantBetween(Double minAmount, Double maxAmount);
    List<Credit> findByMontantGreaterThanEqual(Double minAmount);
    List<Credit> findByMontantLessThanEqual(Double maxAmount);
    
    List<Credit> findByDateDemandeBetween(LocalDate startDate, LocalDate endDate);
    List<Credit> findByDateDemandeGreaterThanEqual(LocalDate startDate);
    List<Credit> findByDateDemandeLessThanEqual(LocalDate endDate);
}
