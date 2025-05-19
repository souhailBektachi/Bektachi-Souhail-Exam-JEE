package com.souhailbektachi.backend.repositories;

import com.souhailbektachi.backend.entities.CreditProfessionnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditProfessionnelRepository extends JpaRepository<CreditProfessionnel, Long> {
    List<CreditProfessionnel> findByRaisonSocialeEntrepriseContaining(String keyword);
}
