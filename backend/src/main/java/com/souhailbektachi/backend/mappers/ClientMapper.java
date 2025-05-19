package com.souhailbektachi.backend.mappers;

import com.souhailbektachi.backend.dtos.ClientDTO;
import com.souhailbektachi.backend.dtos.ClientRequestDTO;
import com.souhailbektachi.backend.dtos.ClientSummaryDTO;
import com.souhailbektachi.backend.dtos.CreditSummaryDTO;
import com.souhailbektachi.backend.entities.Client;
import com.souhailbektachi.backend.entities.Credit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    private final CreditMapper creditMapper;

    @Autowired
    public ClientMapper(CreditMapper creditMapper) {
        this.creditMapper = creditMapper;
    }

    /**
     * Convert a Client entity to a ClientDTO
     */
    public ClientDTO toDto(Client client) {
        if (client == null) {
            return null;
        }

        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setEmail(client.getEmail());
        
        // Map credits if they exist
        if (client.getCredits() != null && !client.getCredits().isEmpty()) {
            List<CreditSummaryDTO> creditSummaries = client.getCredits().stream()
                    .map(creditMapper::toSummaryDto)
                    .collect(Collectors.toList());
            dto.setCredits(creditSummaries);
        } else {
            dto.setCredits(Collections.emptyList());
        }
        
        return dto;
    }

    /**
     * Convert a Client entity to a ClientSummaryDTO with credit count
     */
    public ClientSummaryDTO toSummaryDto(Client client) {
        if (client == null) {
            return null;
        }

        ClientSummaryDTO dto = new ClientSummaryDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setEmail(client.getEmail());
        
        // Count the number of credits
        int creditCount = (client.getCredits() != null) ? client.getCredits().size() : 0;
        dto.setNombreCredits(creditCount);
        
        return dto;
    }

    /**
     * Convert a ClientRequestDTO to a new Client entity
     */
    public Client toEntity(ClientRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Client client = new Client();
        client.setNom(requestDTO.getNom());
        client.setEmail(requestDTO.getEmail());
        // Credits will be empty for a new client
        client.setCredits(Collections.emptyList());
        
        return client;
    }

    /**
     * Update an existing Client entity from a ClientRequestDTO
     */
    public void updateClientFromDto(ClientRequestDTO requestDTO, Client client) {
        if (requestDTO == null || client == null) {
            return;
        }

        if (requestDTO.getNom() != null) {
            client.setNom(requestDTO.getNom());
        }
        if (requestDTO.getEmail() != null) {
            client.setEmail(requestDTO.getEmail());
        }
        // We don't update credits through this method to maintain relationship integrity
    }

    /**
     * Convert a list of Client entities to ClientDTOs
     */
    public List<ClientDTO> toDtoList(List<Client> clients) {
        if (clients == null) {
            return Collections.emptyList();
        }
        
        return clients.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of Client entities to ClientSummaryDTOs
     */
    public List<ClientSummaryDTO> toSummaryDtoList(List<Client> clients) {
        if (clients == null) {
            return Collections.emptyList();
        }
        
        return clients.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }
}
