package com.souhailbektachi.backend.services.impl;

import com.souhailbektachi.backend.dtos.*;
import com.souhailbektachi.backend.entities.*;
import com.souhailbektachi.backend.exceptions.BadRequestException;
import com.souhailbektachi.backend.exceptions.ResourceNotFoundException;
import com.souhailbektachi.backend.mappers.CreditMapper;
import com.souhailbektachi.backend.repositories.ClientRepository;
import com.souhailbektachi.backend.repositories.CreditRepository;
import com.souhailbektachi.backend.services.CreditService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final ClientRepository clientRepository;
    private final CreditMapper creditMapper;

    @Override
    public List<CreditSummaryDTO> getAllCredits() {
        List<Credit> credits = creditRepository.findAll();
        return creditMapper.toSummaryDtoList(credits);
    }

    @Override
    public CreditDTO getCreditById(Long id) {
        Credit credit = findCreditOrThrow(id);
        return creditMapper.toDto(credit);
    }

    @Override
    public CreditDTO createCredit(CreditRequestDTO creditRequestDTO) {
        validateCreditRequest(creditRequestDTO);
        
        // Validate client exists
        Client client = clientRepository.findById(creditRequestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", creditRequestDTO.getClientId()));
        
        Credit credit = creditMapper.toEntity(creditRequestDTO);
        credit.setClient(client);
        credit.setDateDemande(LocalDate.now());
        credit.setStatut(StatutCredit.EN_COURS);
        
        Credit savedCredit = creditRepository.save(credit);
        
        return creditMapper.toDto(savedCredit);
    }

    @Override
    public CreditDTO updateCredit(Long id, CreditRequestDTO creditRequestDTO) {
        Credit credit = findCreditOrThrow(id);
        
        // Only allow updates for credits that are still in progress
        if (credit.getStatut() != StatutCredit.EN_COURS) {
            throw new BadRequestException("Cannot update credit that is not in progress");
        }
        
        validateCreditRequest(creditRequestDTO);
        
        // Update the credit
        creditMapper.updateCreditFromDto(creditRequestDTO, credit);
        Credit updatedCredit = creditRepository.save(credit);
        
        return creditMapper.toDto(updatedCredit);
    }

    @Override
    public void deleteCredit(Long id) {
        Credit credit = findCreditOrThrow(id);
        
        // Only allow deletion of credits that are still in progress or rejected
        if (credit.getStatut() == StatutCredit.ACCEPTE) {
            throw new BadRequestException("Cannot delete an accepted credit");
        }
        
        // Check if the credit has any repayments
        if (credit.getRemboursements() != null && !credit.getRemboursements().isEmpty()) {
            throw new BadRequestException("Cannot delete a credit with repayments");
        }
        
        creditRepository.delete(credit);
    }

    @Override
    public List<CreditSummaryDTO> getCreditsByStatus(StatutCredit status) {
        List<Credit> credits = creditRepository.findByStatut(status);
        return creditMapper.toSummaryDtoList(credits);
    }

    @Override
    public List<CreditSummaryDTO> getCreditsByType(String type) {
        List<Credit> credits;
        
        switch (type.toUpperCase()) {
            case "PERSONNEL":
                credits = creditRepository.findCreditPersonnel();
                break;
            case "IMMOBILIER":
                credits = creditRepository.findCreditImmobilier();
                break;
            case "PROFESSIONNEL":
                credits = creditRepository.findCreditProfessionnel();
                break;
            default:
                throw new BadRequestException("Invalid credit type: " + type);
        }
        
        return creditMapper.toSummaryDtoList(credits);
    }

    @Override
    public CreditDTO approveCredit(Long id, LocalDate approvalDate) {
        Credit credit = findCreditOrThrow(id);
        
        // Validate credit state
        if (credit.getStatut() != StatutCredit.EN_COURS) {
            throw new BadRequestException("Only credits in progress can be approved");
        }
        
        // Validate approval date is not before request date
        if (approvalDate.isBefore(credit.getDateDemande())) {
            throw new BadRequestException("Approval date cannot be before request date");
        }
        
        // Update credit status
        credit.setStatut(StatutCredit.ACCEPTE);
        credit.setDateAcception(approvalDate);
        
        Credit updatedCredit = creditRepository.save(credit);
        
        return creditMapper.toDto(updatedCredit);
    }

    @Override
    public CreditDTO rejectCredit(Long id, String reason) {
        Credit credit = findCreditOrThrow(id);
        
        // Validate credit state
        if (credit.getStatut() != StatutCredit.EN_COURS) {
            throw new BadRequestException("Only credits in progress can be rejected");
        }
        
        // Update credit status
        credit.setStatut(StatutCredit.REJETE);
        
        // Store rejection reason in a comment or extend the model to include a reason field
        
        Credit updatedCredit = creditRepository.save(credit);
        
        return creditMapper.toDto(updatedCredit);
    }

    @Override
    public Map<String, Object> calculateMonthlyPayment(Long id) {
        Credit credit = findCreditOrThrow(id);
        
        // Calculate monthly payment using the formula:
        // M = P * (r * (1 + r)^n) / ((1 + r)^n - 1)
        // where M is the monthly payment, P is the principal (loan amount),
        // r is the monthly interest rate, and n is the number of payments (loan duration in months)
        
        double principal = credit.getMontant();
        double annualInterestRate = credit.getTauxInteret() / 100.0; // Convert percentage to decimal
        double monthlyInterestRate = annualInterestRate / 12.0;
        int loanDurationMonths = credit.getDureeRemboursement();
        
        double monthlyPayment;
        if (monthlyInterestRate == 0) {
            // If interest rate is 0, just divide the principal by the number of months
            monthlyPayment = principal / loanDurationMonths;
        } else {
            // Standard formula for monthly payment calculation
            double temp = Math.pow(1 + monthlyInterestRate, loanDurationMonths);
            monthlyPayment = principal * (monthlyInterestRate * temp / (temp - 1));
        }
        
        // Format to 2 decimal places
        monthlyPayment = Math.round(monthlyPayment * 100.0) / 100.0;
        
        // Calculate total payment and total interest
        double totalPayment = monthlyPayment * loanDurationMonths;
        double totalInterest = totalPayment - principal;
        
        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("creditId", credit.getId());
        result.put("montant", principal);
        result.put("dureeRemboursement", loanDurationMonths);
        result.put("tauxInteret", credit.getTauxInteret());
        result.put("mensualite", monthlyPayment);
        result.put("totalInterets", totalInterest);
        result.put("totalPaiement", totalPayment);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getPaymentSchedule(Long id) {
        Credit credit = findCreditOrThrow(id);
        
        Map<String, Object> paymentInfo = calculateMonthlyPayment(id);
        double monthlyPayment = (Double) paymentInfo.get("mensualite");
        
        double principal = credit.getMontant();
        double annualInterestRate = credit.getTauxInteret() / 100.0;
        double monthlyInterestRate = annualInterestRate / 12.0;
        int loanDurationMonths = credit.getDureeRemboursement();
        
        // Start date is either the acceptance date or current date if not accepted
        LocalDate startDate = credit.getDateAcception() != null ? 
                credit.getDateAcception() : LocalDate.now();
        
        List<Map<String, Object>> schedule = new ArrayList<>();
        double remainingBalance = principal;
        
        for (int month = 1; month <= loanDurationMonths; month++) {
            LocalDate paymentDate = startDate.plusMonths(month);
            double interestPayment = remainingBalance * monthlyInterestRate;
            double principalPayment = monthlyPayment - interestPayment;
            
            // Adjust the last payment to account for rounding errors
            if (month == loanDurationMonths) {
                principalPayment = remainingBalance;
                monthlyPayment = principalPayment + interestPayment;
            }
            
            remainingBalance -= principalPayment;
            
            // Format to 2 decimal places
            interestPayment = Math.round(interestPayment * 100.0) / 100.0;
            principalPayment = Math.round(principalPayment * 100.0) / 100.0;
            remainingBalance = Math.round(remainingBalance * 100.0) / 100.0;
            
            Map<String, Object> payment = new HashMap<>();
            payment.put("numeroPaiement", month);
            payment.put("datePaiement", paymentDate);
            payment.put("montantTotal", monthlyPayment);
            payment.put("montantPrincipal", principalPayment);
            payment.put("montantInteret", interestPayment);
            payment.put("soldeRestant", remainingBalance);
            
            schedule.add(payment);
        }
        
        return schedule;
    }

    @Override
    public Map<String, Object> validateCreditApplication(CreditRequestDTO creditRequestDTO) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        
        try {
            validateCreditRequest(creditRequestDTO);
            
            // Additional validations can be added here based on credit type
            if (creditRequestDTO instanceof CreditPersonnelRequestDTO) {
                validatePersonalCredit((CreditPersonnelRequestDTO) creditRequestDTO, errors);
            } else if (creditRequestDTO instanceof CreditImmobilierRequestDTO) {
                validateRealEstateCredit((CreditImmobilierRequestDTO) creditRequestDTO, errors);
            } else if (creditRequestDTO instanceof CreditProfessionnelRequestDTO) {
                validateBusinessCredit((CreditProfessionnelRequestDTO) creditRequestDTO, errors);
            }
            
            // Client eligibility check
            if (creditRequestDTO.getClientId() != null) {
                checkClientEligibility(creditRequestDTO.getClientId(), errors);
            }
            
            result.put("isValid", errors.isEmpty());
            result.put("errors", errors);
            
            return result;
        } catch (BadRequestException e) {
            result.put("isValid", false);
            errors.add(e.getMessage());
            result.put("errors", errors);
            return result;
        }
    }

    @Override
    public List<CreditSummaryDTO> searchCreditsByAmountRange(Double minAmount, Double maxAmount) {
        List<Credit> credits;
        
        if (minAmount != null && maxAmount != null) {
            credits = creditRepository.findByMontantBetween(minAmount, maxAmount);
        } else if (minAmount != null) {
            credits = creditRepository.findByMontantGreaterThanEqual(minAmount);
        } else if (maxAmount != null) {
            credits = creditRepository.findByMontantLessThanEqual(maxAmount);
        } else {
            credits = creditRepository.findAll();
        }
        
        return creditMapper.toSummaryDtoList(credits);
    }

    @Override
    public List<CreditSummaryDTO> searchCreditsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Credit> credits;
        
        if (startDate != null && endDate != null) {
            credits = creditRepository.findByDateDemandeBetween(startDate, endDate);
        } else if (startDate != null) {
            credits = creditRepository.findByDateDemandeGreaterThanEqual(startDate);
        } else if (endDate != null) {
            credits = creditRepository.findByDateDemandeLessThanEqual(endDate);
        } else {
            credits = creditRepository.findAll();
        }
        
        return creditMapper.toSummaryDtoList(credits);
    }
    
    // Helper methods
    
    private Credit findCreditOrThrow(Long id) {
        return creditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit", "id", id));
    }
    
    private void validateCreditRequest(CreditRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new BadRequestException("Credit request cannot be null");
        }
        
        // Client ID validation
        if (requestDTO.getClientId() == null) {
            throw new BadRequestException("Client ID is required");
        }
        
        // Amount validation
        if (requestDTO.getMontant() == null || requestDTO.getMontant() <= 0) {
            throw new BadRequestException("Credit amount must be positive");
        }
        
        // Duration validation
        if (requestDTO.getDureeRemboursement() == null || requestDTO.getDureeRemboursement() <= 0) {
            throw new BadRequestException("Credit duration must be positive");
        }
        
        // Interest rate validation
        if (requestDTO.getTauxInteret() == null || requestDTO.getTauxInteret() < 0) {
            throw new BadRequestException("Credit interest rate cannot be negative");
        }
    }
    
    private void validatePersonalCredit(CreditPersonnelRequestDTO requestDTO, List<String> errors) {
        if (requestDTO.getMotif() == null || requestDTO.getMotif().trim().isEmpty()) {
            errors.add("Personal credit must have a reason");
        }
        
        // Additional business rules for personal credits
        if (requestDTO.getMontant() > 100000) {
            errors.add("Personal credit amount cannot exceed 100,000");
        }
        
        if (requestDTO.getDureeRemboursement() > 60) {
            errors.add("Personal credit duration cannot exceed 60 months (5 years)");
        }
    }
    
    private void validateRealEstateCredit(CreditImmobilierRequestDTO requestDTO, List<String> errors) {
        if (requestDTO.getTypeBienFinance() == null) {
            errors.add("Real estate credit must specify property type");
        }
        
        // Additional business rules for real estate credits
        if (requestDTO.getMontant() > 1000000) {
            errors.add("Real estate credit amount cannot exceed 1,000,000");
        }
        
        if (requestDTO.getDureeRemboursement() > 300) {
            errors.add("Real estate credit duration cannot exceed 300 months (25 years)");
        }
    }
    
    private void validateBusinessCredit(CreditProfessionnelRequestDTO requestDTO, List<String> errors) {
        if (requestDTO.getMotif() == null || requestDTO.getMotif().trim().isEmpty()) {
            errors.add("Business credit must have a reason");
        }
        
        if (requestDTO.getRaisonSocialeEntreprise() == null || requestDTO.getRaisonSocialeEntreprise().trim().isEmpty()) {
            errors.add("Business credit must specify company name");
        }
        
        // Additional business rules for business credits
        if (requestDTO.getMontant() > 500000) {
            errors.add("Business credit amount cannot exceed 500,000");
        }
        
        if (requestDTO.getDureeRemboursement() > 120) {
            errors.add("Business credit duration cannot exceed 120 months (10 years)");
        }
    }
    
    private void checkClientEligibility(Long clientId, List<String> errors) {
        Client client = clientRepository.findById(clientId).orElse(null);
        
        if (client == null) {
            errors.add("Client not found");
            return;
        }
        
        // Check if client has too many active credits
        long activeCreditsCount = client.getCredits().stream()
                .filter(c -> c.getStatut() == StatutCredit.EN_COURS || c.getStatut() == StatutCredit.ACCEPTE)
                .count();
        
        if (activeCreditsCount >= 3) {
            errors.add("Client already has 3 or more active credits");
        }
        
        // Additional eligibility checks can be added here
    }
}
