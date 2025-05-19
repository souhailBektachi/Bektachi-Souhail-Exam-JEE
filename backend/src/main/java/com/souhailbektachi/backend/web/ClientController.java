package com.souhailbektachi.backend.web;

import com.souhailbektachi.backend.dtos.ClientDTO;
import com.souhailbektachi.backend.dtos.ClientRequestDTO;
import com.souhailbektachi.backend.dtos.ClientSummaryDTO;
import com.souhailbektachi.backend.dtos.CreditSummaryDTO;
import com.souhailbektachi.backend.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Client management API")
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Get all clients", description = "Returns a list of all clients with summary information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of clients",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientSummaryDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ClientSummaryDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @Operation(summary = "Get client by ID", description = "Returns detailed information for a specific client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the client",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class))),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @Operation(summary = "Create new client", description = "Creates a new client and returns the created client details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(
            @Parameter(description = "Client data", required = true)
            @Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        return new ResponseEntity<>(clientService.createClient(clientRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update client", description = "Updates an existing client and returns the updated client details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated client data", required = true)
            @Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        return ResponseEntity.ok(clientService.updateClient(id, clientRequestDTO));
    }

    @Operation(summary = "Delete client", description = "Deletes a client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "400", description = "Client cannot be deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get client credits", description = "Returns all credits for a specific client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved client credits",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditSummaryDTO.class))),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{id}/credits")
    public ResponseEntity<List<CreditSummaryDTO>> getClientCredits(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientCredits(id));
    }

    @Operation(summary = "Search clients by name", description = "Returns clients matching the specified name keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching clients",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientSummaryDTO.class)))
    })
    @GetMapping("/search/name")
    public ResponseEntity<List<ClientSummaryDTO>> searchClientsByName(
            @Parameter(description = "Name keyword", required = true)
            @RequestParam String keyword) {
        return ResponseEntity.ok(clientService.searchClientsByName(keyword));
    }

    @Operation(summary = "Search clients by email", description = "Returns clients matching the specified email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matching clients",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientSummaryDTO.class)))
    })
    @GetMapping("/search/email")
    public ResponseEntity<List<ClientSummaryDTO>> searchClientsByEmail(
            @Parameter(description = "Email address", required = true)
            @RequestParam String email) {
        return ResponseEntity.ok(clientService.searchClientsByEmail(email));
    }
}
