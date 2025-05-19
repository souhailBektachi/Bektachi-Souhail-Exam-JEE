package com.souhailbektachi.backend.repositories;

import com.souhailbektachi.backend.entities.CreditPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditPersonnelRepository extends JpaRepository<CreditPersonnel, Long> {
    // You can add custom query methods here
}
