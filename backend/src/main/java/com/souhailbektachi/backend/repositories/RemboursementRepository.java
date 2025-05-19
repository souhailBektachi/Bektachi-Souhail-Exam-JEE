package com.souhailbektachi.backend.repositories;

import com.souhailbektachi.backend.entities.Remboursement;
import com.souhailbektachi.backend.entities.TypeRemboursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
    List<Remboursement> findByCreditId(Long creditId);
    List<Remboursement> findByType(TypeRemboursement type);
}
