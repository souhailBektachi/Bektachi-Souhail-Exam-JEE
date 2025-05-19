package com.souhailbektachi.backend.services.impl;

import com.souhailbektachi.backend.dtos.RemboursementDTO;
import com.souhailbektachi.backend.dtos.RemboursementRequestDTO;
import com.souhailbektachi.backend.entities.Credit;
import com.souhailbektachi.backend.entities.Remboursement;
import com.souhailbektachi.backend.entities.TypeRemboursement;
import com.souhailbektachi.backend.exceptions.BadRequestException;
import com.souhailbektachi.backend.exceptions.ResourceNotFoundException;
import com.souhailbektachi.backend.mappers.RemboursementMapper;
import com.souhailbektachi.backend.repositories.CreditRepository;
import com.souhailbektachi.backend.repositories.RemboursementRepository;
import com.souhailbektachi.backend.services.RemboursementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RemboursementServiceImpl implements RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final CreditRepository creditRepository;
    private final RemboursementMapper remboursementMapper;

    @Override
    public List<RemboursementDTO> getAllRemboursements() {
        List<Remboursement> remboursements = remboursementRepository.findAll();
        return remboursementMapper.toDtoList(remboursements);
    }

    @Override
    public RemboursementDTO getRemboursementById(Long id) {
        Remboursement remboursement = findRemboursementOrThrow(id);
        return remboursementMapper.toDto(remboursement);
    }

    @Override
    public RemboursementDTO createRemboursement(RemboursementRequestDTO remboursementRequestDTO) {
        validateRemboursementRequest(remboursementRequestDTO);
        
        // Check if credit exists
        Credit credit = creditRepository.findById(remboursementRequestDTO.getCreditId())
                .orElseThrow(() -> new ResourceNotFoundException("Credit", "id", remboursementRequestDTO.getCreditId()));
        
        Remboursement remboursement = remboursementMapper.toEntity(remboursementRequestDTO);
        Remboursement savedRemboursement = remboursementRepository.save(remboursement);
        
        return remboursementMapper.toDto(savedRemboursement);
    }

    @Override
    public RemboursementDTO updateRemboursement(Long id, RemboursementRequestDTO remboursementRequestDTO) {
        Remboursement remboursement = findRemboursementOrThrow(id);
        validateRemboursementRequest(remboursementRequestDTO);
        
        remboursementMapper.updateRemboursementFromDto(remboursementRequestDTO, remboursement);
        Remboursement updatedRemboursement = remboursementRepository.save(remboursement);
        
        return remboursementMapper.toDto(updatedRemboursement);
    }

    @Override
    public void deleteRemboursement(Long id) {
        Remboursement remboursement = findRemboursementOrThrow(id);
        remboursementRepository.delete(remboursement);
    }

    @Override
    public List<RemboursementDTO> getRemboursementsByCreditId(Long creditId) {
        // Check if credit exists
        if (!creditRepository.existsById(creditId)) {
            throw new ResourceNotFoundException("Credit", "id", creditId);
        }
        
        List<Remboursement> remboursements = remboursementRepository.findByCreditId(creditId);
        return remboursementMapper.toDtoList(remboursements);
    }

    @Override
    public List<RemboursementDTO> getRemboursementsByType(TypeRemboursement type) {
        List<Remboursement> remboursements = remboursementRepository.findByType(type);
        return remboursementMapper.toDtoList(remboursements);
    }

    @Override
    public Map<String, Object> processEarlyRepayment(Long creditId, Double amount, LocalDate date) {
        // Verify credit exists
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit", "id", creditId));
        
        // Create early repayment
        Remboursement remboursement = new Remboursement();
        remboursement.setCredit(credit);
        remboursement.setDate(date);
        remboursement.setMontant(amount);
        remboursement.setType(TypeRemboursement.REMBOURSEMENT_ANTICIPE);
        
        Remboursement savedRemboursement = remboursementRepository.save(remboursement);
        
        // Calculate new credit balance after early repayment
        Map<String, Object> remainingBalance = calculateRemainingBalance(creditId);
        Map<String, Object> result = new HashMap<>();
        result.put("remboursement", remboursementMapper.toDto(savedRemboursement));
        result.put("creditBalance", remainingBalance);
        
        return result;
    }

    @Override
    public RemboursementDTO processMonthlyInstallment(Long creditId, LocalDate date) {
        // Verify credit exists
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit", "id", creditId));
        
        // Calculate monthly payment amount (simplified version - this should use the same formula as in CreditService)
        double principal = credit.getMontant();
        double annualInterestRate = credit.getTauxInteret() / 100.0;
        double monthlyInterestRate = annualInterestRate / 12.0;
        int loanDurationMonths = credit.getDureeRemboursement();
        
        double monthlyPayment;
        if (monthlyInterestRate == 0) {
            monthlyPayment = principal / loanDurationMonths;
        } else {
            double temp = Math.pow(1 + monthlyInterestRate, loanDurationMonths);
            monthlyPayment = principal * (monthlyInterestRate * temp / (temp - 1));
        }
        
        // Round to 2 decimal places
        monthlyPayment = Math.round(monthlyPayment * 100.0) / 100.0;
        
        // Create monthly payment record
        Remboursement remboursement = new Remboursement();
        remboursement.setCredit(credit);
        remboursement.setDate(date);
        remboursement.setMontant(monthlyPayment);
        remboursement.setType(TypeRemboursement.MENSUALITE);
        
        Remboursement savedRemboursement = remboursementRepository.save(remboursement);
        
        return remboursementMapper.toDto(savedRemboursement);
    }

    @Override
    public Map<String, Object> calculateRemainingBalance(Long creditId) {
        // Verify credit exists
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit", "id", creditId));
        
        // Get all repayments for this credit
        List<Remboursement> remboursements = remboursementRepository.findByCreditId(creditId);
        
        // Calculate total amount repaid
        double totalRepaid = remboursements.stream()
                .mapToDouble(Remboursement::getMontant)
                .sum();
        
        // Calculate original total to be paid (principal + interest)
        double principal = credit.getMontant();
        double annualInterestRate = credit.getTauxInteret() / 100.0;
        double monthlyInterestRate = annualInterestRate / 12.0;
        int loanDurationMonths = credit.getDureeRemboursement();
        
        double monthlyPayment;
        if (monthlyInterestRate == 0) {
            monthlyPayment = principal / loanDurationMonths;
        } else {
            double temp = Math.pow(1 + monthlyInterestRate, loanDurationMonths);
            monthlyPayment = principal * (monthlyInterestRate * temp / (temp - 1));
        }
        
        double totalPayment = monthlyPayment * loanDurationMonths;
        double totalInterest = totalPayment - principal;
        
        // Calculate remaining balance
        double remainingBalance = totalPayment - totalRepaid;
        if (remainingBalance < 0) remainingBalance = 0;
        
        // Round to 2 decimal places
        totalRepaid = Math.round(totalRepaid * 100.0) / 100.0;
        totalPayment = Math.round(totalPayment * 100.0) / 100.0;
        totalInterest = Math.round(totalInterest * 100.0) / 100.0;
        remainingBalance = Math.round(remainingBalance * 100.0) / 100.0;
        
        // Count payments by type
        long regularPayments = remboursements.stream()
                .filter(r -> r.getType() == TypeRemboursement.MENSUALITE)
                .count();
        
        long earlyPayments = remboursements.stream()
                .filter(r -> r.getType() == TypeRemboursement.REMBOURSEMENT_ANTICIPE)
                .count();
        
        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("creditId", credit.getId());
        result.put("montantInitial", principal);
        result.put("montantTotalAvecInterets", totalPayment);
        result.put("interetsTotaux", totalInterest);
        result.put("montantRembourse", totalRepaid);
        result.put("soldeRestant", remainingBalance);
        result.put("nombrePaiementsMensuels", regularPayments);
        result.put("nombreRemboursementsAnticipes", earlyPayments);
        
        return result;
    }

    @Override
    public List<RemboursementDTO> searchRemboursementsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Remboursement> remboursements;
        
        if (startDate != null && endDate != null) {
            remboursements = remboursementRepository.findByDateBetween(startDate, endDate);
        } else if (startDate != null) {
            remboursements = remboursementRepository.findByDateGreaterThanEqual(startDate);
        } else if (endDate != null) {
            remboursements = remboursementRepository.findByDateLessThanEqual(endDate);
        } else {
            remboursements = remboursementRepository.findAll();
        }
        
        return remboursementMapper.toDtoList(remboursements);
    }

    @Override
    public List<RemboursementDTO> searchRemboursementsByAmountRange(Double minAmount, Double maxAmount) {
        List<Remboursement> remboursements;
        
        if (minAmount != null && maxAmount != null) {
            remboursements = remboursementRepository.findByMontantBetween(minAmount, maxAmount);
        } else if (minAmount != null) {
            remboursements = remboursementRepository.findByMontantGreaterThanEqual(minAmount);
        } else if (maxAmount != null) {
            remboursements = remboursementRepository.findByMontantLessThanEqual(maxAmount);
        } else {
            remboursements = remboursementRepository.findAll();
        }
        
        return remboursementMapper.toDtoList(remboursements);
    }
    
    // Helper methods
    
    private Remboursement findRemboursementOrThrow(Long id) {
        return remboursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remboursement", "id", id));
    }
    
    private void validateRemboursementRequest(RemboursementRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new BadRequestException("Remboursement request cannot be null");
        }
        
        if (requestDTO.getCreditId() == null) {
            throw new BadRequestException("Credit ID is required");
        }
        
        if (requestDTO.getDate() == null) {
            throw new BadRequestException("Remboursement date is required");
        }
        
        if (requestDTO.getMontant() == null || requestDTO.getMontant() <= 0) {
            throw new BadRequestException("Remboursement amount must be positive");
        }
        
        if (requestDTO.getType() == null) {
            throw new BadRequestException("Remboursement type is required");
        }
    }
}
