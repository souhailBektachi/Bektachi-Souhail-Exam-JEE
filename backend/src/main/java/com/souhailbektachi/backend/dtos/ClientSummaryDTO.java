package com.souhailbektachi.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryDTO {
    private Long id;
    private String nom;
    private String email;
    private int nombreCredits;
}
