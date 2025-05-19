package com.souhailbektachi.backend.repositories;

import com.souhailbektachi.backend.entities.CreditImmobilier;
import com.souhailbektachi.backend.entities.TypeBienImmobilier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditImmobilierRepository extends JpaRepository<CreditImmobilier, Long> {
    List<CreditImmobilier> findByTypeBienFinance(TypeBienImmobilier typeBienFinance);
}
