package com.souhailbektachi.backend.repositories;

import com.souhailbektachi.backend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // You can add custom query methods here
}
