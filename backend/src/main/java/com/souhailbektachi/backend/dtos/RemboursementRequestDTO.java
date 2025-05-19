package com.souhailbektachi.backend.dtos;

import com.souhailbektachi.backend.entities.TypeRemboursement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemboursementRequestDTO {
    private LocalDate date;
    private Double montant;
    private TypeRemboursement type;
    private Long creditId;
}
