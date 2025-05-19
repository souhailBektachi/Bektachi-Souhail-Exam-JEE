package com.souhailbektachi.backend.mappers;

import com.souhailbektachi.backend.dtos.RemboursementDTO;
import com.souhailbektachi.backend.dtos.RemboursementRequestDTO;
import com.souhailbektachi.backend.entities.Credit;
import com.souhailbektachi.backend.entities.Remboursement;
import com.souhailbektachi.backend.repositories.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemboursementMapper {

    private final CreditRepository creditRepository;

    @Autowired
    public RemboursementMapper(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    /**
     * Convert a Remboursement entity to a RemboursementDTO
     */
    public RemboursementDTO toDto(Remboursement remboursement) {
        if (remboursement == null) {
            return null;
        }

        RemboursementDTO dto = new RemboursementDTO();
        dto.setId(remboursement.getId());
        dto.setDate(remboursement.getDate());
        dto.setMontant(remboursement.getMontant());
        dto.setType(remboursement.getType());
        
        // Set credit ID if credit exists
        if (remboursement.getCredit() != null) {
            dto.setCreditId(remboursement.getCredit().getId());
        }
        
        return dto;
    }

    /**
     * Convert a RemboursementRequestDTO to a new Remboursement entity
     */
    public Remboursement toEntity(RemboursementRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Remboursement remboursement = new Remboursement();
        remboursement.setDate(requestDTO.getDate());
        remboursement.setMontant(requestDTO.getMontant());
        remboursement.setType(requestDTO.getType());
        
        // Set credit if credit ID is provided
        if (requestDTO.getCreditId() != null) {
            Credit credit = creditRepository.findById(requestDTO.getCreditId()).orElse(null);
            remboursement.setCredit(credit);
        }
        
        return remboursement;
    }

    /**
     * Update an existing Remboursement entity from a RemboursementRequestDTO
     */
    public void updateRemboursementFromDto(RemboursementRequestDTO requestDTO, Remboursement remboursement) {
        if (requestDTO == null || remboursement == null) {
            return;
        }

        if (requestDTO.getDate() != null) {
            remboursement.setDate(requestDTO.getDate());
        }
        if (requestDTO.getMontant() != null) {
            remboursement.setMontant(requestDTO.getMontant());
        }
        if (requestDTO.getType() != null) {
            remboursement.setType(requestDTO.getType());
        }
        
        // Update credit if ID is provided and different from current
        if (requestDTO.getCreditId() != null && 
                (remboursement.getCredit() == null || !remboursement.getCredit().getId().equals(requestDTO.getCreditId()))) {
            creditRepository.findById(requestDTO.getCreditId())
                    .ifPresent(remboursement::setCredit);
        }
    }

    /**
     * Convert a list of Remboursement entities to DTOs
     */
    public List<RemboursementDTO> toDtoList(List<Remboursement> remboursements) {
        if (remboursements == null) {
            return Collections.emptyList();
        }
        
        return remboursements.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
