package com.souhailbektachi.backend.services.impl;

import com.souhailbektachi.backend.dtos.ClientDTO;
import com.souhailbektachi.backend.dtos.ClientRequestDTO;
import com.souhailbektachi.backend.dtos.ClientSummaryDTO;
import com.souhailbektachi.backend.dtos.CreditSummaryDTO;
import com.souhailbektachi.backend.entities.Client;
import com.souhailbektachi.backend.entities.Credit;
import com.souhailbektachi.backend.entities.StatutCredit;
import com.souhailbektachi.backend.exceptions.BadRequestException;
import com.souhailbektachi.backend.exceptions.ResourceNotFoundException;
import com.souhailbektachi.backend.mappers.ClientMapper;
import com.souhailbektachi.backend.mappers.CreditMapper;
import com.souhailbektachi.backend.repositories.ClientRepository;
import com.souhailbektachi.backend.repositories.CreditRepository;
import com.souhailbektachi.backend.services.ClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final ClientMapper clientMapper;
    private final CreditMapper creditMapper;

    @Override
    public List<ClientSummaryDTO> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return clientMapper.toSummaryDtoList(clients);
    }

    @Override
    public ClientDTO getClientById(Long id) {
        Client client = findClientOrThrow(id);
        return clientMapper.toDto(client);
    }

    @Override
    public ClientDTO createClient(ClientRequestDTO clientRequestDTO) {
        validateClientRequest(clientRequestDTO);
        
        // Check if email is already in use
        if (clientRepository.findByEmail(clientRequestDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already in use");
        }
        
        Client client = clientMapper.toEntity(clientRequestDTO);
        Client savedClient = clientRepository.save(client);
        
        return clientMapper.toDto(savedClient);
    }

    @Override
    public ClientDTO updateClient(Long id, ClientRequestDTO clientRequestDTO) {
        validateClientRequest(clientRequestDTO);
        
        Client client = findClientOrThrow(id);
        
        // Check if email is already in use by another client
        if (clientRepository.findByEmail(clientRequestDTO.getEmail())
                .filter(c -> !c.getId().equals(id))
                .isPresent()) {
            throw new BadRequestException("Email is already in use by another client");
        }
        
        clientMapper.updateClientFromDto(clientRequestDTO, client);
        Client updatedClient = clientRepository.save(client);
        
        return clientMapper.toDto(updatedClient);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = findClientOrThrow(id);
        
        // Check if client has active credits
        boolean hasActiveCredits = client.getCredits().stream()
                .anyMatch(credit -> credit.getStatut() == StatutCredit.EN_COURS || 
                                    credit.getStatut() == StatutCredit.ACCEPTE);
        
        if (hasActiveCredits) {
            throw new BadRequestException("Cannot delete client with active credits");
        }
        
        clientRepository.delete(client);
    }

    @Override
    public List<CreditSummaryDTO> getClientCredits(Long clientId) {
        Client client = findClientOrThrow(clientId);
        List<Credit> credits = creditRepository.findByClientId(clientId);
        
        return creditMapper.toSummaryDtoList(credits);
    }

    @Override
    public List<ClientSummaryDTO> searchClientsByName(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return getAllClients();
        }
        
        List<Client> clients = clientRepository.findByNomContainingIgnoreCase(keyword);
        return clientMapper.toSummaryDtoList(clients);
    }

    @Override
    public List<ClientSummaryDTO> searchClientsByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return getAllClients();
        }
        
        List<Client> clients = clientRepository.findByEmailContainingIgnoreCase(email);
        return clientMapper.toSummaryDtoList(clients);
    }
    
    // Helper methods
    
    private Client findClientOrThrow(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
    }
    
    private void validateClientRequest(ClientRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new BadRequestException("Client request cannot be null");
        }
        
        if (StringUtils.isBlank(requestDTO.getNom())) {
            throw new BadRequestException("Client name cannot be empty");
        }
        
        if (StringUtils.isBlank(requestDTO.getEmail())) {
            throw new BadRequestException("Client email cannot be empty");
        }
        
        // Basic email validation
        if (!requestDTO.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BadRequestException("Invalid email format");
        }
    }
}
