package com.souhailbektachi.backend.web;

import com.souhailbektachi.backend.dtos.CreditDTO;
import com.souhailbektachi.backend.dtos.CreditRequestDTO;
import com.souhailbektachi.backend.dtos.CreditSummaryDTO;
import com.souhailbektachi.backend.entities.StatutCredit;
import com.souhailbektachi.backend.services.CreditService;
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
@RequestMapping("/api/credits")
@RequiredArgsConstructor
@Tag(name = "Credit", description = "Credit management API")
@CrossOrigin(origins = "*")
public class CreditController {

    private final CreditService creditService;

    @Operation(summary = "Get all credits", description = "Returns a list of all credits with summary information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of credits",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditSummaryDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<CreditSummaryDTO>> getAllCredits() {
        return ResponseEntity.ok(creditService.getAllCredits());
    }

    @Operation(summary = "Get credit by ID", description = "Returns detailed information for a specific credit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the credit",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditDTO.class))),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreditDTO> getCreditById(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(creditService.getCreditById(id));
    }

    @Operation(summary = "Create new credit", description = "Creates a new credit application and returns the created credit details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Credit successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<CreditDTO> createCredit(
            @Parameter(description = "Credit data", required = true)
            @Valid @RequestBody CreditRequestDTO creditRequestDTO) {
        return new ResponseEntity<>(creditService.createCredit(creditRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update credit", description = "Updates an existing credit and returns the updated credit details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or credit cannot be updated"),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CreditDTO> updateCredit(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated credit data", required = true)
            @Valid @RequestBody CreditRequestDTO creditRequestDTO) {
        return ResponseEntity.ok(creditService.updateCredit(id, creditRequestDTO));
    }

    @Operation(summary = "Delete credit", description = "Deletes a credit by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Credit successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Credit not found"),
            @ApiResponse(responseCode = "400", description = "Credit cannot be deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredit(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long id) {
        creditService.deleteCredit(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get credits by status", description = "Returns credits with the specified status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching credits",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditSummaryDTO.class)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CreditSummaryDTO>> getCreditsByStatus(
            @Parameter(description = "Credit status", required = true)
            @PathVariable StatutCredit status) {
        return ResponseEntity.ok(creditService.getCreditsByStatus(status));
    }

    @Operation(summary = "Get credits by type", description = "Returns credits of the specified type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching credits",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditSummaryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credit type")
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CreditSummaryDTO>> getCreditsByType(
            @Parameter(description = "Credit type (PERSONNEL, IMMOBILIER, PROFESSIONNEL)", required = true)
            @PathVariable String type) {
        return ResponseEntity.ok(creditService.getCreditsByType(type));
    }

    @Operation(summary = "Approve credit", description = "Approves a credit application and returns the updated credit details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit successfully approved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditDTO.class))),
            @ApiResponse(responseCode = "400", description = "Credit cannot be approved"),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @PutMapping("/{id}/approve")
    public ResponseEntity<CreditDTO> approveCredit(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Approval date (defaults to current date if not provided)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate approvalDate) {
        LocalDate date = approvalDate != null ? approvalDate : LocalDate.now();
        return ResponseEntity.ok(creditService.approveCredit(id, date));
    }

    @Operation(summary = "Reject credit", description = "Rejects a credit application and returns the updated credit details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit successfully rejected",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditDTO.class))),
            @ApiResponse(responseCode = "400", description = "Credit cannot be rejected"),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @PutMapping("/{id}/reject")
    public ResponseEntity<CreditDTO> rejectCredit(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Rejection reason")
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(creditService.rejectCredit(id, reason));
    }

    @Operation(summary = "Calculate monthly payment", description = "Calculates the monthly payment for a credit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated monthly payment",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @GetMapping("/{id}/monthly-payment")
    public ResponseEntity<Map<String, Object>> calculateMonthlyPayment(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(creditService.calculateMonthlyPayment(id));
    }

    @Operation(summary = "Get payment schedule", description = "Returns the detailed payment schedule for a credit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved payment schedule",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    @GetMapping("/{id}/payment-schedule")
    public ResponseEntity<List<Map<String, Object>>> getPaymentSchedule(
            @Parameter(description = "Credit ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(creditService.getPaymentSchedule(id));
    }

    @Operation(summary = "Validate credit application", description = "Validates a credit application before submission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation results",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCreditApplication(
            @Parameter(description = "Credit data to validate", required = true)
            @Valid @RequestBody CreditRequestDTO creditRequestDTO) {
        return ResponseEntity.ok(creditService.validateCreditApplication(creditRequestDTO));
    }

    @Operation(summary = "Search credits by amount range", description = "Returns credits within the specified amount range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching credits",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditSummaryDTO.class)))
    })
    @GetMapping("/search/amount")
    public ResponseEntity<List<CreditSummaryDTO>> searchCreditsByAmountRange(
            @Parameter(description = "Minimum amount")
            @RequestParam(required = false) Double minAmount,
            @Parameter(description = "Maximum amount")
            @RequestParam(required = false) Double maxAmount) {
        return ResponseEntity.ok(creditService.searchCreditsByAmountRange(minAmount, maxAmount));
    }

    @Operation(summary = "Search credits by date range", description = "Returns credits within the specified date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching credits",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditSummaryDTO.class)))
    })
    @GetMapping("/search/date")
    public ResponseEntity<List<CreditSummaryDTO>> searchCreditsByDateRange(
            @Parameter(description = "Start date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(creditService.searchCreditsByDateRange(startDate, endDate));
    }
}
