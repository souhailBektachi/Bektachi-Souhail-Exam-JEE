package com.souhailbektachi.backend.services;

import com.souhailbektachi.backend.dtos.RemboursementDTO;
import com.souhailbektachi.backend.dtos.RemboursementRequestDTO;
import com.souhailbektachi.backend.entities.TypeRemboursement;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for operations related to repayments
 */
public interface RemboursementService {

    /**
     * Retrieve all repayments
     * 
     * @return List of all repayments
     */
    List<RemboursementDTO> getAllRemboursements();

    /**
     * Get detailed information for a specific repayment
     * 
     * @param id The repayment's ID
     * @return Complete repayment data
     * @throws RuntimeException if repayment is not found
     */
    RemboursementDTO getRemboursementById(Long id);

    /**
     * Create a new repayment
     * 
     * @param remboursementRequestDTO The repayment data to create
     * @return The created repayment with ID assigned
     * @throws RuntimeException if validation fails
     */
    RemboursementDTO createRemboursement(RemboursementRequestDTO remboursementRequestDTO);

    /**
     * Update an existing repayment
     * 
     * @param id The repayment's ID
     * @param remboursementRequestDTO The updated repayment data
     * @return The updated repayment information
     * @throws RuntimeException if repayment is not found or validation fails
     */
    RemboursementDTO updateRemboursement(Long id, RemboursementRequestDTO remboursementRequestDTO);

    /**
     * Delete a repayment by ID
     * 
     * @param id The repayment's ID
     * @throws RuntimeException if repayment is not found
     */
    void deleteRemboursement(Long id);

    /**
     * Get all repayments for a specific credit
     * 
     * @param creditId The credit's ID
     * @return List of repayments associated with the credit
     * @throws RuntimeException if credit is not found
     */
    List<RemboursementDTO> getRemboursementsByCreditId(Long creditId);
    
    /**
     * Filter repayments by type
     * 
     * @param type The repayment type
     * @return List of repayments of the specified type
     */
    List<RemboursementDTO> getRemboursementsByType(TypeRemboursement type);
    
    /**
     * Process an early repayment for a credit
     * 
     * @param creditId The credit's ID
     * @param amount The early repayment amount
     * @param date The repayment date
     * @return The created repayment information and updated credit details
     * @throws RuntimeException if credit is not found or validation fails
     */
    Map<String, Object> processEarlyRepayment(Long creditId, Double amount, LocalDate date);
    
    /**
     * Process a monthly installment for a credit
     * 
     * @param creditId The credit's ID
     * @param date The payment date
     * @return The created repayment information
     * @throws RuntimeException if credit is not found or validation fails
     */
    RemboursementDTO processMonthlyInstallment(Long creditId, LocalDate date);
    
    /**
     * Calculate the remaining balance of a credit after all repayments
     * 
     * @param creditId The credit's ID
     * @return The remaining balance and payment statistics
     * @throws RuntimeException if credit is not found
     */
    Map<String, Object> calculateRemainingBalance(Long creditId);
    
    /**
     * Search for repayments by date range
     * 
     * @param startDate Start date (optional)
     * @param endDate End date (optional)
     * @return List of repayments within the specified date range
     */
    List<RemboursementDTO> searchRemboursementsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Search for repayments by amount range
     * 
     * @param minAmount Minimum amount (optional)
     * @param maxAmount Maximum amount (optional)
     * @return List of repayments within the specified amount range
     */
    List<RemboursementDTO> searchRemboursementsByAmountRange(Double minAmount, Double maxAmount);
}
