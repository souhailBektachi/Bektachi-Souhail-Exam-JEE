package com.souhailbektachi.backend.dtos;

import com.souhailbektachi.backend.entities.StatutCredit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditSummaryDTO {
    private Long id;
    private LocalDate dateDemande;
    private StatutCredit statut;
    private Double montant;
    private Integer dureeRemboursement;
    private String type; // Type of credit: "PERSONNEL", "IMMOBILIER", "PROFESSIONNEL"
    
    // Additional fields for specific credit types
    private String motif; // For PERSONNEL and PROFESSIONNEL
    private String typeBienFinance; // For IMMOBILIER
    private String raisonSocialeEntreprise; // For PROFESSIONNEL
}
