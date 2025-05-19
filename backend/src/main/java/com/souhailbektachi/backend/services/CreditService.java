package com.souhailbektachi.backend.services;

import com.souhailbektachi.backend.dtos.*;
import com.souhailbektachi.backend.entities.StatutCredit;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for operations related to credits
 */
public interface CreditService {

    /**
     * Retrieve all credits
     * 
     * @return List of all credits with summary information
     */
    List<CreditSummaryDTO> getAllCredits();

    /**
     * Get detailed information for a specific credit
     * 
     * @param id The credit's ID
     * @return Complete credit data including customer and repayment information
     * @throws RuntimeException if credit is not found
     */
    CreditDTO getCreditById(Long id);

    /**
     * Create a new credit of any type
     * 
     * @param creditRequestDTO The credit data to create (specific subtype)
     * @return The created credit with ID assigned
     * @throws RuntimeException if validation fails
     */
    CreditDTO createCredit(CreditRequestDTO creditRequestDTO);

    /**
     * Update an existing credit
     * 
     * @param id The credit's ID
     * @param creditRequestDTO The updated credit data
     * @return The updated credit information
     * @throws RuntimeException if credit is not found or validation fails
     */
    CreditDTO updateCredit(Long id, CreditRequestDTO creditRequestDTO);

    /**
     * Delete a credit by ID
     * 
     * @param id The credit's ID
     * @throws RuntimeException if credit is not found or cannot be deleted
     */
    void deleteCredit(Long id);

    /**
     * Filter credits by status
     * 
     * @param status The status to filter by
     * @return List of credits with the specified status
     */
    List<CreditSummaryDTO> getCreditsByStatus(StatutCredit status);
    
    /**
     * Filter credits by type
     * 
     * @param type The credit type: "PERSONNEL", "IMMOBILIER", "PROFESSIONNEL"
     * @return List of credits of the specified type
     */
    List<CreditSummaryDTO> getCreditsByType(String type);
    
    /**
     * Approve a credit application
     * 
     * @param id The credit's ID
     * @param approvalDate The date of approval
     * @return The updated credit with approved status
     * @throws RuntimeException if credit is not found or not in pending status
     */
    CreditDTO approveCredit(Long id, LocalDate approvalDate);
    
    /**
     * Reject a credit application
     * 
     * @param id The credit's ID
     * @param reason Reason for rejection (optional)
     * @return The updated credit with rejected status
     * @throws RuntimeException if credit is not found or not in pending status
     */
    CreditDTO rejectCredit(Long id, String reason);
    
    /**
     * Calculate monthly payment for a credit
     * 
     * @param id The credit's ID
     * @return Calculation result with payment details
     * @throws RuntimeException if credit is not found
     */
    Map<String, Object> calculateMonthlyPayment(Long id);
    
    /**
     * Get detailed payment schedule for a credit
     * 
     * @param id The credit's ID
     * @return List of scheduled payments with amounts and dates
     * @throws RuntimeException if credit is not found
     */
    List<Map<String, Object>> getPaymentSchedule(Long id);
    
    /**
     * Validate a new credit application
     * 
     * @param creditRequestDTO The credit data to validate
     * @return Validation result with success flag and any error messages
     */
    Map<String, Object> validateCreditApplication(CreditRequestDTO creditRequestDTO);
    
    /**
     * Search for credits by amount range
     * 
     * @param minAmount Minimum amount (optional)
     * @param maxAmount Maximum amount (optional)
     * @return List of credits within the specified amount range
     */
    List<CreditSummaryDTO> searchCreditsByAmountRange(Double minAmount, Double maxAmount);
    
    /**
     * Search for credits by date range
     * 
     * @param startDate Start date (optional)
     * @param endDate End date (optional)
     * @return List of credits within the specified date range
     */
    List<CreditSummaryDTO> searchCreditsByDateRange(LocalDate startDate, LocalDate endDate);
}
