package com.souhailbektachi.backend.web;

import com.souhailbektachi.backend.dtos.RemboursementDTO;
import com.souhailbektachi.backend.dtos.RemboursementRequestDTO;
import com.souhailbektachi.backend.entities.TypeRemboursement;
import com.souhailbektachi.backend.services.RemboursementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/remboursements")
@RequiredArgsConstructor
@Tag(name = "Remboursement", description = "Repayment management API")
@CrossOrigin(origins = "*")
public class RemboursementController {

    private final RemboursementService remboursementService;

    @Operation(summary = "Get all repayments", description = "Returns a list of all repayments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of repayments",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<RemboursementDTO>> getAllRemboursements() {
        return ResponseEntity.ok(remboursementService.getAllRemboursements());
    }

    @Operation(summary = "Get repayment by ID", description = "Returns detailed information for a specific repayment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the repayment",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class))),
            @ApiResponse(responseCode = "404", description = "Repayment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RemboursementDTO> getRemboursementById(
            @Parameter(description = "Repayment ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(remboursementService.getRemboursementById(id));
    }

    @Operation(summary = "Create new repayment", description = "Creates a new repayment and returns the created repayment details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Repayment successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<RemboursementDTO> createRemboursement(
            @Parameter(description = "Repayment data", required = true)
            @Valid @RequestBody RemboursementRequestDTO remboursementRequestDTO) {
        return new ResponseEntity<>(remboursementService.createRemboursement(remboursementRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update repayment", description = "Updates an existing repayment and returns the updated repayment details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repayment successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Repayment not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RemboursementDTO> updateRemboursement(
            @Parameter(description = "Repayment ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated repayment data", required = true)
            @Valid @RequestBody RemboursementRequestDTO remboursementRequestDTO) {
        return ResponseEntity.ok(remboursementService.updateRemboursement(id, remboursementRequestDTO));
    }

    @Operation(summary = "Delete repayment", description = "Deletes a repayment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Repayment successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Repayment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRemboursement(
            @Parameter(description = "Repayment ID", required = true)
            @PathVariable Long id) {
        remboursementService.deleteRemboursement(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get repayments by credit ID", description = "Returns all repayments for a specific credit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved credit repayments",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class))),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @GetMapping("/credit/{creditId}")
    public ResponseEntity<List<RemboursementDTO>> getRemboursementsByCreditId(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long creditId) {
        return ResponseEntity.ok(remboursementService.getRemboursementsByCreditId(creditId));
    }

    @Operation(summary = "Get repayments by type", description = "Returns repayments of the specified type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching repayments",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class)))
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<List<RemboursementDTO>> getRemboursementsByType(
            @Parameter(description = "Repayment type", required = true)
            @PathVariable TypeRemboursement type) {
        return ResponseEntity.ok(remboursementService.getRemboursementsByType(type));
    }

    @Operation(summary = "Process early repayment", description = "Records an early repayment for a credit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Early repayment successfully processed",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @PostMapping("/early-repayment")
    public ResponseEntity<Map<String, Object>> processEarlyRepayment(
            @Parameter(description = "Credit ID", required = true)
            @RequestParam Long creditId,
            @Parameter(description = "Repayment amount", required = true)
            @RequestParam Double amount,
            @Parameter(description = "Repayment date (defaults to current date if not provided)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate repaymentDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(remboursementService.processEarlyRepayment(creditId, amount, repaymentDate));
    }

    @Operation(summary = "Process monthly installment", description = "Records a monthly installment payment for a credit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Monthly installment successfully processed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @PostMapping("/monthly-installment")
    public ResponseEntity<RemboursementDTO> processMonthlyInstallment(
            @Parameter(description = "Credit ID", required = true)
            @RequestParam Long creditId,
            @Parameter(description = "Payment date (defaults to current date if not provided)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate paymentDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(remboursementService.processMonthlyInstallment(creditId, paymentDate));
    }

    @Operation(summary = "Calculate remaining balance", description = "Calculates the remaining balance for a credit after all repayments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated remaining balance",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @GetMapping("/remaining-balance/{creditId}")
    public ResponseEntity<Map<String, Object>> calculateRemainingBalance(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long creditId) {
        return ResponseEntity.ok(remboursementService.calculateRemainingBalance(creditId));
    }

    @Operation(summary = "Search repayments by date range", description = "Returns repayments within the specified date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching repayments",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class)))
    })
    @GetMapping("/search/date")
    public ResponseEntity<List<RemboursementDTO>> searchRemboursementsByDateRange(
            @Parameter(description = "Start date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(remboursementService.searchRemboursementsByDateRange(startDate, endDate));
    }

    @Operation(summary = "Search repayments by amount range", description = "Returns repayments within the specified amount range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching repayments",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RemboursementDTO.class)))
    })
    @GetMapping("/search/amount")
    public ResponseEntity<List<RemboursementDTO>> searchRemboursementsByAmountRange(
            @Parameter(description = "Minimum amount")
            @RequestParam(required = false) Double minAmount,
            @Parameter(description = "Maximum amount")
            @RequestParam(required = false) Double maxAmount) {
        return ResponseEntity.ok(remboursementService.searchRemboursementsByAmountRange(minAmount, maxAmount));
    }
}
