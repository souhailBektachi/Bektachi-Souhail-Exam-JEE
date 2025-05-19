package com.souhailbektachi.backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for reporting and analytics operations
 */
public interface ReportingService {

    /**
     * Generate a summary report of all credits grouped by status
     * 
     * @return Map with status as key and list of credits as value
     */
    Map<String, Object> getCreditSummaryByStatus();
    
    /**
     * Generate a summary report of all credits grouped by type
     * 
     * @return Map with credit type as key and list of credits as value
     */
    Map<String, Object> getCreditSummaryByType();
    
    /**
     * Generate statistics on repayments
     * 
     * @param startDate Start date for the report period (optional)
     * @param endDate End date for the report period (optional)
     * @return Map with various statistics on repayments
     */
    Map<String, Object> getRepaymentStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get monthly report on new credit applications
     * 
     * @param year The year for the report
     * @return Monthly breakdown of credit applications with amounts and counts
     */
    List<Map<String, Object>> getMonthlyCreditApplicationsReport(int year);
    
    /**
     * Generate client activity report
     * 
     * @param clientId The client's ID
     * @return Activity statistics for the specified client
     * @throws RuntimeException if client is not found
     */
    Map<String, Object> getClientActivityReport(Long clientId);
    
    /**
     * Generate report on credit performance by type
     * 
     * @return Performance metrics for each credit type
     */
    Map<String, Object> getCreditPerformanceByType();
    
    /**
     * Generate report on delinquent loans
     * 
     * @return List of credits with missed or late payments
     */
    List<Map<String, Object>> getDelinquentLoansReport();
    
    /**
     * Generate dashboard summary with key metrics
     * 
     * @return Map with various key metrics for dashboard display
     */
    Map<String, Object> getDashboardSummary();
}
