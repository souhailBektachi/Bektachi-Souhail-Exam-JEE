package com.souhailbektachi.backend.mappers;

import com.souhailbektachi.backend.dtos.*;
import com.souhailbektachi.backend.entities.*;
import com.souhailbektachi.backend.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreditMapper {

    private final ClientRepository clientRepository;
    private final RemboursementMapper remboursementMapper;

    @Autowired
    public CreditMapper(ClientRepository clientRepository, RemboursementMapper remboursementMapper) {
        this.clientRepository = clientRepository;
        this.remboursementMapper = remboursementMapper;
    }

    /**
     * Convert a Credit entity to the appropriate DTO based on its type
     */
    public CreditDTO toDto(Credit credit) {
        if (credit == null) {
            return null;
        }

        // Base credit information common to all types
        CreditDTO baseDto = new CreditDTO();
        baseDto.setId(credit.getId());
        baseDto.setDateDemande(credit.getDateDemande());
        baseDto.setStatut(credit.getStatut());
        baseDto.setDateAcception(credit.getDateAcception());
        baseDto.setMontant(credit.getMontant());
        baseDto.setDureeRemboursement(credit.getDureeRemboursement());
        baseDto.setTauxInteret(credit.getTauxInteret());
        
        // Set client summary information
        if (credit.getClient() != null) {
            ClientSummaryDTO clientSummary = new ClientSummaryDTO();
            clientSummary.setId(credit.getClient().getId());
            clientSummary.setNom(credit.getClient().getNom());
            clientSummary.setEmail(credit.getClient().getEmail());
            clientSummary.setNombreCredits(credit.getClient().getCredits() != null ? 
                    credit.getClient().getCredits().size() : 0);
            baseDto.setClient(clientSummary);
        }
        
        // Set remboursements if they exist
        if (credit.getRemboursements() != null && !credit.getRemboursements().isEmpty()) {
            List<RemboursementDTO> remboursementDTOs = credit.getRemboursements().stream()
                    .map(remboursementMapper::toDto)
                    .collect(Collectors.toList());
            baseDto.setRemboursements(remboursementDTOs);
        } else {
            baseDto.setRemboursements(Collections.emptyList());
        }

        // Return the appropriate DTO based on credit type
        if (credit instanceof CreditPersonnel) {
            CreditPersonnel personnelCredit = (CreditPersonnel) credit;
            CreditPersonnelDTO dto = new CreditPersonnelDTO(baseDto, personnelCredit.getMotif());
            return dto;
        } else if (credit instanceof CreditImmobilier) {
            CreditImmobilier immobilierCredit = (CreditImmobilier) credit;
            CreditImmobilierDTO dto = new CreditImmobilierDTO(baseDto, immobilierCredit.getTypeBienFinance());
            return dto;
        } else if (credit instanceof CreditProfessionnel) {
            CreditProfessionnel professionnelCredit = (CreditProfessionnel) credit;
            CreditProfessionnelDTO dto = new CreditProfessionnelDTO(baseDto, 
                    professionnelCredit.getMotif(), professionnelCredit.getRaisonSocialeEntreprise());
            return dto;
        }
        
        // If not a specific type, return the base DTO
        return baseDto;
    }

    /**
     * Convert a Credit entity to a summary DTO with only essential information
     */
    public CreditSummaryDTO toSummaryDto(Credit credit) {
        if (credit == null) {
            return null;
        }

        CreditSummaryDTO summary = new CreditSummaryDTO();
        summary.setId(credit.getId());
        summary.setDateDemande(credit.getDateDemande());
        summary.setStatut(credit.getStatut());
        summary.setMontant(credit.getMontant());
        summary.setDureeRemboursement(credit.getDureeRemboursement());
        
        // Set type-specific fields
        if (credit instanceof CreditPersonnel) {
            CreditPersonnel personnelCredit = (CreditPersonnel) credit;
            summary.setType("PERSONNEL");
            summary.setMotif(personnelCredit.getMotif());
        } else if (credit instanceof CreditImmobilier) {
            CreditImmobilier immobilierCredit = (CreditImmobilier) credit;
            summary.setType("IMMOBILIER");
            summary.setTypeBienFinance(immobilierCredit.getTypeBienFinance().toString());
        } else if (credit instanceof CreditProfessionnel) {
            CreditProfessionnel professionnelCredit = (CreditProfessionnel) credit;
            summary.setType("PROFESSIONNEL");
            summary.setMotif(professionnelCredit.getMotif());
            summary.setRaisonSocialeEntreprise(professionnelCredit.getRaisonSocialeEntreprise());
        }
        
        return summary;
    }

    /**
     * Convert a CreditRequestDTO to the appropriate Credit entity based on its type
     */
    public Credit toEntity(CreditRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Credit credit;
        
        // Create the appropriate credit type based on the request DTO class
        if (requestDTO instanceof CreditPersonnelRequestDTO) {
            CreditPersonnelRequestDTO personnelRequest = (CreditPersonnelRequestDTO) requestDTO;
            CreditPersonnel personnelCredit = new CreditPersonnel();
            personnelCredit.setMotif(personnelRequest.getMotif());
            credit = personnelCredit;
        } else if (requestDTO instanceof CreditImmobilierRequestDTO) {
            CreditImmobilierRequestDTO immobilierRequest = (CreditImmobilierRequestDTO) requestDTO;
            CreditImmobilier immobilierCredit = new CreditImmobilier();
            immobilierCredit.setTypeBienFinance(immobilierRequest.getTypeBienFinance());
            credit = immobilierCredit;
        } else if (requestDTO instanceof CreditProfessionnelRequestDTO) {
            CreditProfessionnelRequestDTO professionnelRequest = (CreditProfessionnelRequestDTO) requestDTO;
            CreditProfessionnel professionnelCredit = new CreditProfessionnel();
            professionnelCredit.setMotif(professionnelRequest.getMotif());
            professionnelCredit.setRaisonSocialeEntreprise(professionnelRequest.getRaisonSocialeEntreprise());
            credit = professionnelCredit;
        } else {
            // Default to a base credit (though this should not happen with proper validation)
            return null;
        }
        
        // Set common fields
        credit.setMontant(requestDTO.getMontant());
        credit.setDureeRemboursement(requestDTO.getDureeRemboursement());
        credit.setTauxInteret(requestDTO.getTauxInteret());
        credit.setDateDemande(LocalDate.now());
        credit.setStatut(StatutCredit.EN_COURS);
        credit.setRemboursements(Collections.emptyList());
        
        // Set client if client ID is provided
        if (requestDTO.getClientId() != null) {
            clientRepository.findById(requestDTO.getClientId())
                    .ifPresent(credit::setClient);
        }
        
        return credit;
    }

    /**
     * Update an existing Credit entity from a CreditRequestDTO
     */
    public void updateCreditFromDto(CreditRequestDTO requestDTO, Credit credit) {
        if (requestDTO == null || credit == null) {
            return;
        }

        // Update common fields if they're provided
        if (requestDTO.getMontant() != null) {
            credit.setMontant(requestDTO.getMontant());
        }
        if (requestDTO.getDureeRemboursement() != null) {
            credit.setDureeRemboursement(requestDTO.getDureeRemboursement());
        }
        if (requestDTO.getTauxInteret() != null) {
            credit.setTauxInteret(requestDTO.getTauxInteret());
        }
        
        // Update client if ID is provided and different from current
        if (requestDTO.getClientId() != null && 
                (credit.getClient() == null || !credit.getClient().getId().equals(requestDTO.getClientId()))) {
            clientRepository.findById(requestDTO.getClientId())
                    .ifPresent(credit::setClient);
        }
        
        // Update type-specific fields
        if (requestDTO instanceof CreditPersonnelRequestDTO && credit instanceof CreditPersonnel) {
            CreditPersonnelRequestDTO personnelRequest = (CreditPersonnelRequestDTO) requestDTO;
            CreditPersonnel personnelCredit = (CreditPersonnel) credit;
            
            if (personnelRequest.getMotif() != null) {
                personnelCredit.setMotif(personnelRequest.getMotif());
            }
        } else if (requestDTO instanceof CreditImmobilierRequestDTO && credit instanceof CreditImmobilier) {
            CreditImmobilierRequestDTO immobilierRequest = (CreditImmobilierRequestDTO) requestDTO;
            CreditImmobilier immobilierCredit = (CreditImmobilier) credit;
            
            if (immobilierRequest.getTypeBienFinance() != null) {
                immobilierCredit.setTypeBienFinance(immobilierRequest.getTypeBienFinance());
            }
        } else if (requestDTO instanceof CreditProfessionnelRequestDTO && credit instanceof CreditProfessionnel) {
            CreditProfessionnelRequestDTO professionnelRequest = (CreditProfessionnelRequestDTO) requestDTO;
            CreditProfessionnel professionnelCredit = (CreditProfessionnel) credit;
            
            if (professionnelRequest.getMotif() != null) {
                professionnelCredit.setMotif(professionnelRequest.getMotif());
            }
            if (professionnelRequest.getRaisonSocialeEntreprise() != null) {
                professionnelCredit.setRaisonSocialeEntreprise(professionnelRequest.getRaisonSocialeEntreprise());
            }
        }
    }

    /**
     * Convert a list of Credit entities to DTOs
     */
    public List<CreditDTO> toDtoList(List<Credit> credits) {
        if (credits == null) {
            return Collections.emptyList();
        }
        
        return credits.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of Credit entities to summary DTOs
     */
    public List<CreditSummaryDTO> toSummaryDtoList(List<Credit> credits) {
        if (credits == null) {
            return Collections.emptyList();
        }
        
        return credits.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }
}
