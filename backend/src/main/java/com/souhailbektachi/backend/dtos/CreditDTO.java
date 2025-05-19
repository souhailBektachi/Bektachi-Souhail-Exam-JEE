package com.souhailbektachi.backend.dtos;

import com.souhailbektachi.backend.entities.StatutCredit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDTO {
    private Long id;
    private LocalDate dateDemande;
    private StatutCredit statut;
    private LocalDate dateAcception;
    private Double montant;
    private Integer dureeRemboursement;
    private Double tauxInteret;
    private ClientSummaryDTO client;
    private List<RemboursementDTO> remboursements;
    private String type; // Type of credit: "PERSONNEL", "IMMOBILIER", "PROFESSIONNEL"
}
