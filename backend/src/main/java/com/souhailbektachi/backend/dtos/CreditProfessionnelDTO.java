package com.souhailbektachi.backend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreditProfessionnelDTO extends CreditDTO {
    private String motif;
    private String raisonSocialeEntreprise;
    
    public CreditProfessionnelDTO(CreditDTO creditDTO, String motif, String raisonSocialeEntreprise) {
        super(creditDTO.getId(), creditDTO.getDateDemande(), creditDTO.getStatut(), 
              creditDTO.getDateAcception(), creditDTO.getMontant(), creditDTO.getDureeRemboursement(), 
              creditDTO.getTauxInteret(), creditDTO.getClient(), creditDTO.getRemboursements(), "PROFESSIONNEL");
        this.motif = motif;
        this.raisonSocialeEntreprise = raisonSocialeEntreprise;
    }
}
