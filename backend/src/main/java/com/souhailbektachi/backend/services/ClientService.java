package com.souhailbektachi.backend.services;

import com.souhailbektachi.backend.dtos.ClientDTO;
import com.souhailbektachi.backend.dtos.ClientRequestDTO;
import com.souhailbektachi.backend.dtos.ClientSummaryDTO;
import com.souhailbektachi.backend.dtos.CreditSummaryDTO;

import java.util.List;

/**
 * Service interface for operations related to clients
 */
public interface ClientService {

    /**
     * Retrieve all clients
     * 
     * @return List of all clients with their summary information
     */
    List<ClientSummaryDTO> getAllClients();

    /**
     * Get detailed information for a specific client
     * 
     * @param id The client's ID
     * @return Complete client data including credit information
     * @throws RuntimeException if client is not found
     */
    ClientDTO getClientById(Long id);

    /**
     * Create a new client
     * 
     * @param clientRequestDTO The client data to create
     * @return The created client with ID assigned
     */
    ClientDTO createClient(ClientRequestDTO clientRequestDTO);

    /**
     * Update an existing client
     * 
     * @param id The client's ID
     * @param clientRequestDTO The updated client data
     * @return The updated client information
     * @throws RuntimeException if client is not found
     */
    ClientDTO updateClient(Long id, ClientRequestDTO clientRequestDTO);

    /**
     * Delete a client by ID
     * 
     * @param id The client's ID
     * @throws RuntimeException if client is not found or has active credits
     */
    void deleteClient(Long id);

    /**
     * Get all credits for a specific client
     * 
     * @param clientId The client's ID
     * @return List of credits associated with the client
     * @throws RuntimeException if client is not found
     */
    List<CreditSummaryDTO> getClientCredits(Long clientId);
    
    /**
     * Search for clients by name
     * 
     * @param keyword The name (or part of the name) to search for
     * @return List of clients matching the search criteria
     */
    List<ClientSummaryDTO> searchClientsByName(String keyword);
    
    /**
     * Search for clients by email
     * 
     * @param email The email address to search for
     * @return List of clients matching the email address
     */
    List<ClientSummaryDTO> searchClientsByEmail(String email);
}
