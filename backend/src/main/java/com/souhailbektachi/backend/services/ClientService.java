package com.souhailbektachi.backend.services;

import com.souhailbektachi.backend.dtos.ClientDTO;
import com.souhailbektachi.backend.dtos.ClientRequestDTO;
import com.souhailbektachi.backend.dtos.ClientSummaryDTO;
import com.souhailbektachi.backend.dtos.CreditSummaryDTO;

import java.util.List;

public interface ClientService {

    List<ClientSummaryDTO> getAllClients();

    ClientDTO getClientById(Long id);

    ClientDTO createClient(ClientRequestDTO clientRequestDTO);

    ClientDTO updateClient(Long id, ClientRequestDTO clientRequestDTO);

    void deleteClient(Long id);

    List<CreditSummaryDTO> getClientCredits(Long clientId);
    
    List<ClientSummaryDTO> searchClientsByName(String keyword);
    
    List<ClientSummaryDTO> searchClientsByEmail(String email);

    /**
     * Count total number of clients.
     * @return Total number of clients.
     */
    long countClients();
}
