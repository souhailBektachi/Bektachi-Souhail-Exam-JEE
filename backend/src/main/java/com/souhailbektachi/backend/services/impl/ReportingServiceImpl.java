package com.souhailbektachi.backend.services.impl;

import com.souhailbektachi.backend.dtos.CreditSummaryDTO;
import com.souhailbektachi.backend.entities.*;
import com.souhailbektachi.backend.exceptions.ResourceNotFoundException;
import com.souhailbektachi.backend.mappers.CreditMapper;
import com.souhailbektachi.backend.repositories.ClientRepository;
import com.souhailbektachi.backend.repositories.CreditRepository;
import com.souhailbektachi.backend.repositories.RemboursementRepository;
import com.souhailbektachi.backend.services.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final CreditRepository creditRepository;
    private final ClientRepository clientRepository;
    private final RemboursementRepository remboursementRepository;
    private final CreditMapper creditMapper;

    @Override
    public Map<String, Object> getCreditSummaryByStatus() {
        Map<String, Object> result = new HashMap<>();
        
        // Get all credits
        List<Credit> allCredits = creditRepository.findAll();
        
        // Group credits by status
        Map<StatutCredit, List<Credit>> creditsByStatus = allCredits.stream()
                .collect(Collectors.groupingBy(Credit::getStatut));
        
        // Convert each group to DTOs and add to result
        for (Map.Entry<StatutCredit, List<Credit>> entry : creditsByStatus.entrySet()) {
            String status = entry.getKey().name();
            List<CreditSummaryDTO> creditDTOs = creditMapper.toSummaryDtoList(entry.getValue());
            
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("credits", creditDTOs);
            statusData.put("count", creditDTOs.size());
            statusData.put("totalAmount", creditDTOs.stream()
                    .mapToDouble(CreditSummaryDTO::getMontant)
                    .sum());
            
            result.put(status, statusData);
        }
        
        // Add summary statistics
        result.put("totalCredits", allCredits.size());
        result.put("totalAmount", allCredits.stream()
                .mapToDouble(Credit::getMontant)
                .sum());
        
        return result;
    }

    @Override
    public Map<String, Object> getCreditSummaryByType() {
        Map<String, Object> result = new HashMap<>();
        
        // Get all credits
        List<Credit> allCredits = creditRepository.findAll();
        
        // Sort credits by type
        List<Credit> personnelCredits = allCredits.stream()
                .filter(c -> c instanceof CreditPersonnel)
                .collect(Collectors.toList());
        
        List<Credit> immobilierCredits = allCredits.stream()
                .filter(c -> c instanceof CreditImmobilier)
                .collect(Collectors.toList());
        
        List<Credit> professionnelCredits = allCredits.stream()
                .filter(c -> c instanceof CreditProfessionnel)
                .collect(Collectors.toList());
        
        // Add data for each type
        addCreditTypeData(result, "PERSONNEL", personnelCredits);
        addCreditTypeData(result, "IMMOBILIER", immobilierCredits);
        addCreditTypeData(result, "PROFESSIONNEL", professionnelCredits);
        
        // Add summary statistics
        result.put("totalCredits", allCredits.size());
        result.put("totalAmount", allCredits.stream()
                .mapToDouble(Credit::getMontant)
                .sum());
        
        return result;
    }

    @Override
    public Map<String, Object> getRepaymentStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // Get remboursements within date range
        List<Remboursement> remboursements;
        if (startDate != null && endDate != null) {
            remboursements = remboursementRepository.findByDateBetween(startDate, endDate);
            result.put("dateRange", startDate + " to " + endDate);
        } else if (startDate != null) {
            remboursements = remboursementRepository.findByDateGreaterThanEqual(startDate);
            result.put("dateRange", "From " + startDate);
        } else if (endDate != null) {
            remboursements = remboursementRepository.findByDateLessThanEqual(endDate);
            result.put("dateRange", "Until " + endDate);
        } else {
            remboursements = remboursementRepository.findAll();
            result.put("dateRange", "All time");
        }
        
        // Calculate total amount repaid
        double totalAmount = remboursements.stream()
                .mapToDouble(Remboursement::getMontant)
                .sum();
        
        // Group by repayment type
        Map<TypeRemboursement, List<Remboursement>> remboursementsByType = remboursements.stream()
                .collect(Collectors.groupingBy(Remboursement::getType));
        
        // Add statistics
        result.put("totalRepayments", remboursements.size());
        result.put("totalAmount", totalAmount);
        
        // Add type-specific stats
        for (Map.Entry<TypeRemboursement, List<Remboursement>> entry : remboursementsByType.entrySet()) {
            String type = entry.getKey().name();
            List<Remboursement> typeRemboursements = entry.getValue();
            
            Map<String, Object> typeStats = new HashMap<>();
            typeStats.put("count", typeRemboursements.size());
            typeStats.put("amount", typeRemboursements.stream()
                    .mapToDouble(Remboursement::getMontant)
                    .sum());
            typeStats.put("percentage", (typeStats.get("count") != null && remboursements.size() > 0) ? 
                    (int)typeStats.get("count") * 100.0 / remboursements.size() : 0);
            
            result.put(type, typeStats);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getMonthlyCreditApplicationsReport(int year) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Get all credits from the specified year
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<Credit> creditsInYear = creditRepository.findByDateDemandeBetween(startDate, endDate);
        
        // Group credits by month
        Map<Month, List<Credit>> creditsByMonth = creditsInYear.stream()
                .collect(Collectors.groupingBy(credit -> credit.getDateDemande().getMonth()));
        
        // Create a report entry for each month
        for (int month = 1; month <= 12; month++) {
            Month monthEnum = Month.of(month);
            List<Credit> monthCredits = creditsByMonth.getOrDefault(monthEnum, Collections.emptyList());
            
            // Calculate metrics for this month
            int totalCount = monthCredits.size();
            double totalAmount = monthCredits.stream()
                    .mapToDouble(Credit::getMontant)
                    .sum();
            
            // Count by status
            Map<StatutCredit, Long> countByStatus = monthCredits.stream()
                    .collect(Collectors.groupingBy(Credit::getStatut, Collectors.counting()));
            
            // Count by type
            long personnelCount = monthCredits.stream()
                    .filter(c -> c instanceof CreditPersonnel)
                    .count();
            
            long immobilierCount = monthCredits.stream()
                    .filter(c -> c instanceof CreditImmobilier)
                    .count();
            
            long professionnelCount = monthCredits.stream()
                    .filter(c -> c instanceof CreditProfessionnel)
                    .count();
            
            // Create the month report
            Map<String, Object> monthReport = new HashMap<>();
            monthReport.put("year", year);
            monthReport.put("month", month);
            monthReport.put("monthName", monthEnum.toString());
            monthReport.put("totalApplications", totalCount);
            monthReport.put("totalAmount", totalAmount);
            monthReport.put("pendingCount", countByStatus.getOrDefault(StatutCredit.EN_COURS, 0L));
            monthReport.put("acceptedCount", countByStatus.getOrDefault(StatutCredit.ACCEPTE, 0L));
            monthReport.put("rejectedCount", countByStatus.getOrDefault(StatutCredit.REJETE, 0L));
            monthReport.put("personnelCount", personnelCount);
            monthReport.put("immobilierCount", immobilierCount);
            monthReport.put("professionnelCount", professionnelCount);
            
            // Get days in month for calculating daily average
            int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
            monthReport.put("dailyAverage", totalCount / (double) daysInMonth);
            
            result.add(monthReport);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getClientActivityReport(Long clientId) {
        // Verify client exists
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        
        Map<String, Object> result = new HashMap<>();
        
        // Add client info
        result.put("clientId", client.getId());
        result.put("clientName", client.getNom());
        result.put("clientEmail", client.getEmail());
        
        // Get client's credits
        List<Credit> credits = creditRepository.findByClientId(clientId);
        
        // Add credit statistics
        result.put("totalCredits", credits.size());
        result.put("totalCreditAmount", credits.stream()
                .mapToDouble(Credit::getMontant)
                .sum());
        
        // Count by status
        Map<StatutCredit, Long> countByStatus = credits.stream()
                .collect(Collectors.groupingBy(Credit::getStatut, Collectors.counting()));
        
        result.put("pendingCredits", countByStatus.getOrDefault(StatutCredit.EN_COURS, 0L));
        result.put("acceptedCredits", countByStatus.getOrDefault(StatutCredit.ACCEPTE, 0L));
        result.put("rejectedCredits", countByStatus.getOrDefault(StatutCredit.REJETE, 0L));
        
        // Count by type
        long personnelCount = credits.stream()
                .filter(c -> c instanceof CreditPersonnel)
                .count();
        
        long immobilierCount = credits.stream()
                .filter(c -> c instanceof CreditImmobilier)
                .count();
        
        long professionnelCount = credits.stream()
                .filter(c -> c instanceof CreditProfessionnel)
                .count();
        
        result.put("personnelCredits", personnelCount);
        result.put("immobilierCredits", immobilierCount);
        result.put("professionnelCredits", professionnelCount);
        
        // Get repayment statistics
        List<Long> creditIds = credits.stream()
                .map(Credit::getId)
                .collect(Collectors.toList());
        
        List<Remboursement> remboursements = new ArrayList<>();
        for (Long creditId : creditIds) {
            remboursements.addAll(remboursementRepository.findByCreditId(creditId));
        }
        
        result.put("totalRepayments", remboursements.size());
        result.put("totalRepaidAmount", remboursements.stream()
                .mapToDouble(Remboursement::getMontant)
                .sum());
        
        // Repayments by type
        Map<TypeRemboursement, Long> remboursementsByType = remboursements.stream()
                .collect(Collectors.groupingBy(Remboursement::getType, Collectors.counting()));
        
        result.put("monthlyInstallments", remboursementsByType.getOrDefault(TypeRemboursement.MENSUALITE, 0L));
        result.put("earlyRepayments", remboursementsByType.getOrDefault(TypeRemboursement.REMBOURSEMENT_ANTICIPE, 0L));
        
        return result;
    }

    @Override
    public Map<String, Object> getCreditPerformanceByType() {
        Map<String, Object> result = new HashMap<>();
        
        // Get all credits
        List<Credit> allCredits = creditRepository.findAll();
        
        // Group by credit type
        List<Credit> personnelCredits = allCredits.stream()
                .filter(c -> c instanceof CreditPersonnel)
                .collect(Collectors.toList());
        
        List<Credit> immobilierCredits = allCredits.stream()
                .filter(c -> c instanceof CreditImmobilier)
                .collect(Collectors.toList());
        
        List<Credit> professionnelCredits = allCredits.stream()
                .filter(c -> c instanceof CreditProfessionnel)
                .collect(Collectors.toList());
        
        // Calculate and add performance metrics for each type
        result.put("PERSONNEL", calculatePerformanceMetrics(personnelCredits));
        result.put("IMMOBILIER", calculatePerformanceMetrics(immobilierCredits));
        result.put("PROFESSIONNEL", calculatePerformanceMetrics(professionnelCredits));
        result.put("OVERALL", calculatePerformanceMetrics(allCredits));
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getDelinquentLoansReport() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Get all accepted credits
        List<Credit> acceptedCredits = creditRepository.findByStatut(StatutCredit.ACCEPTE);
        
        // For each credit, analyze repayments to check for missed or late payments
        for (Credit credit : acceptedCredits) {
            // Get all repayments for this credit
            List<Remboursement> repayments = remboursementRepository.findByCreditId(credit.getId());
            
            // Calculate expected number of monthly payments by now
            LocalDate startDate = credit.getDateAcception();
            LocalDate now = LocalDate.now();
            
            // Skip if acceptance date is null
            if (startDate == null) {
                continue;
            }
            
            long monthsSinceStart = startDate.until(now).toTotalMonths();
            long expectedMonthlyPayments = Math.min(monthsSinceStart, credit.getDureeRemboursement());
            
            // Count actual monthly payments
            long actualMonthlyPayments = repayments.stream()
                    .filter(r -> r.getType() == TypeRemboursement.MENSUALITE)
                    .count();
            
            // If we have fewer payments than expected, this loan is delinquent
            if (actualMonthlyPayments < expectedMonthlyPayments) {
                Map<String, Object> delinquentLoan = new HashMap<>();
                
                // Add credit info
                delinquentLoan.put("creditId", credit.getId());
                delinquentLoan.put("creditType", getCreditType(credit));
                delinquentLoan.put("montant", credit.getMontant());
                delinquentLoan.put("dateDemande", credit.getDateDemande());
                delinquentLoan.put("dateAcception", credit.getDateAcception());
                
                // Add client info
                Client client = credit.getClient();
                if (client != null) {
                    delinquentLoan.put("clientId", client.getId());
                    delinquentLoan.put("clientName", client.getNom());
                    delinquentLoan.put("clientEmail", client.getEmail());
                }
                
                // Add delinquency info
                delinquentLoan.put("expectedPayments", expectedMonthlyPayments);
                delinquentLoan.put("actualPayments", actualMonthlyPayments);
                delinquentLoan.put("missedPayments", expectedMonthlyPayments - actualMonthlyPayments);
                delinquentLoan.put("lastPaymentDate", repayments.isEmpty() ? null : 
                        repayments.stream()
                            .max(Comparator.comparing(Remboursement::getDate))
                            .get().getDate());
                
                result.add(delinquentLoan);
            }
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> result = new HashMap<>();
        
        // Client statistics
        long totalClients = clientRepository.count();
        result.put("totalClients", totalClients);
        
        // Credit statistics
        List<Credit> allCredits = creditRepository.findAll();
        result.put("totalCredits", allCredits.size());
        result.put("totalCreditAmount", allCredits.stream().mapToDouble(Credit::getMontant).sum());
        
        // Credits by status
        Map<StatutCredit, Long> creditsByStatus = allCredits.stream()
                .collect(Collectors.groupingBy(Credit::getStatut, Collectors.counting()));
        
        result.put("pendingCredits", creditsByStatus.getOrDefault(StatutCredit.EN_COURS, 0L));
        result.put("acceptedCredits", creditsByStatus.getOrDefault(StatutCredit.ACCEPTE, 0L));
        result.put("rejectedCredits", creditsByStatus.getOrDefault(StatutCredit.REJETE, 0L));
        
        // Credits by type
        long personnelCredits = allCredits.stream().filter(c -> c instanceof CreditPersonnel).count();
        long immobilierCredits = allCredits.stream().filter(c -> c instanceof CreditImmobilier).count();
        long professionnelCredits = allCredits.stream().filter(c -> c instanceof CreditProfessionnel).count();
        
        result.put("personnelCredits", personnelCredits);
        result.put("immobilierCredits", immobilierCredits);
        result.put("professionnelCredits", professionnelCredits);
        
        // Repayment statistics
        List<Remboursement> allRepayments = remboursementRepository.findAll();
        result.put("totalRepayments", allRepayments.size());
        result.put("totalRepaidAmount", allRepayments.stream().mapToDouble(Remboursement::getMontant).sum());
        
        // Recent activity
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        List<Credit> recentCredits = creditRepository.findByDateDemandeGreaterThanEqual(oneMonthAgo);
        List<Remboursement> recentRepayments = remboursementRepository.findByDateGreaterThanEqual(oneMonthAgo);
        
        result.put("recentCreditApplications", recentCredits.size());
        result.put("recentRepayments", recentRepayments.size());
        
        // Delinquent loans count
        List<Map<String, Object>> delinquentLoans = getDelinquentLoansReport();
        result.put("delinquentLoans", delinquentLoans.size());
        
        return result;
    }
    
    // Helper methods
    
    private void addCreditTypeData(Map<String, Object> result, String type, List<Credit> credits) {
        Map<String, Object> typeData = new HashMap<>();
        typeData.put("credits", creditMapper.toSummaryDtoList(credits));
        typeData.put("count", credits.size());
        typeData.put("totalAmount", credits.stream().mapToDouble(Credit::getMontant).sum());
        
        // Get status breakdown
        Map<StatutCredit, Long> statusBreakdown = credits.stream()
                .collect(Collectors.groupingBy(Credit::getStatut, Collectors.counting()));
        typeData.put("statusBreakdown", statusBreakdown);
        
        result.put(type, typeData);
    }
    
    private Map<String, Object> calculatePerformanceMetrics(List<Credit> credits) {
        Map<String, Object> metrics = new HashMap<>();
        
        int totalCount = credits.size();
        metrics.put("totalCount", totalCount);
        
        if (totalCount == 0) {
            metrics.put("approvalRate", 0);
            metrics.put("rejectionRate", 0);
            metrics.put("averageAmount", 0);
            metrics.put("averageDuration", 0);
            metrics.put("averageInterestRate", 0);
            return metrics;
        }
        
        // Calculate status rates
        long acceptedCount = credits.stream()
                .filter(c -> c.getStatut() == StatutCredit.ACCEPTE)
                .count();
        
        long rejectedCount = credits.stream()
                .filter(c -> c.getStatut() == StatutCredit.REJETE)
                .count();
        
        metrics.put("approvalRate", acceptedCount * 100.0 / totalCount);
        metrics.put("rejectionRate", rejectedCount * 100.0 / totalCount);
        
        // Calculate averages
        double totalAmount = credits.stream()
                .mapToDouble(Credit::getMontant)
                .sum();
        
        double averageAmount = totalAmount / totalCount;
        
        double averageDuration = credits.stream()
                .mapToInt(Credit::getDureeRemboursement)
                .average()
                .orElse(0);
        
        double averageInterestRate = credits.stream()
                .mapToDouble(Credit::getTauxInteret)
                .average()
                .orElse(0);
        
        metrics.put("totalAmount", totalAmount);
        metrics.put("averageAmount", averageAmount);
        metrics.put("averageDuration", averageDuration);
        metrics.put("averageInterestRate", averageInterestRate);
        
        return metrics;
    }
    
    private String getCreditType(Credit credit) {
        if (credit instanceof CreditPersonnel) {
            return "PERSONNEL";
        } else if (credit instanceof CreditImmobilier) {
            return "IMMOBILIER";
        } else if (credit instanceof CreditProfessionnel) {
            return "PROFESSIONNEL";
        } else {
            return "UNKNOWN";
        }
    }
}
