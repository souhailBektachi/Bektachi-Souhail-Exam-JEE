package com.souhailbektachi.backend.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreditPersonnelRequestDTO.class, name = "PERSONNEL"),
    @JsonSubTypes.Type(value = CreditImmobilierRequestDTO.class, name = "IMMOBILIER"),
    @JsonSubTypes.Type(value = CreditProfessionnelRequestDTO.class, name = "PROFESSIONNEL")
})
public abstract class CreditRequestDTO {
    private Double montant;
    private Integer dureeRemboursement;
    private Double tauxInteret;
    private Long clientId;
}
