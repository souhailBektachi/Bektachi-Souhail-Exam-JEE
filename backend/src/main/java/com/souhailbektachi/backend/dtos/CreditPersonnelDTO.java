package com.souhailbektachi.backend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreditPersonnelDTO extends CreditDTO {
    private String motif;
    
    public CreditPersonnelDTO(CreditDTO creditDTO, String motif) {
        super(creditDTO.getId(), creditDTO.getDateDemande(), creditDTO.getStatut(), 
              creditDTO.getDateAcception(), creditDTO.getMontant(), creditDTO.getDureeRemboursement(), 
              creditDTO.getTauxInteret(), creditDTO.getClient(), creditDTO.getRemboursements(), "PERSONNEL");
        this.motif = motif;
    }
}
