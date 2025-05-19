package com.souhailbektachi.backend.repositories;

import com.souhailbektachi.backend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
    List<Client> findByNomContainingIgnoreCase(String keyword);
    List<Client> findByEmailContainingIgnoreCase(String email);
}
