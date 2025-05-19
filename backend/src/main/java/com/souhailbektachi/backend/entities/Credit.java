package com.souhailbektachi.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate dateDemande;
    
    @Enumerated(EnumType.STRING)
    private StatutCredit statut;
    
    private LocalDate dateAcception;
    private Double montant;
    private Integer dureeRemboursement;
    private Double tauxInteret;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @OneToMany(mappedBy = "credit", cascade = CascadeType.ALL)
    private List<Remboursement> remboursements = new ArrayList<>();
}
