package com.souhailbektachi.backend.dtos;

import com.souhailbektachi.backend.entities.TypeBienImmobilier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreditImmobilierDTO extends CreditDTO {
    private TypeBienImmobilier typeBienFinance;
    
    public CreditImmobilierDTO(CreditDTO creditDTO, TypeBienImmobilier typeBienFinance) {
        super(creditDTO.getId(), creditDTO.getDateDemande(), creditDTO.getStatut(), 
              creditDTO.getDateAcception(), creditDTO.getMontant(), creditDTO.getDureeRemboursement(), 
              creditDTO.getTauxInteret(), creditDTO.getClient(), creditDTO.getRemboursements(), "IMMOBILIER");
        this.typeBienFinance = typeBienFinance;
    }
}
