package com.souhailbektachi.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Remboursement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate date;
    private Double montant;
    
    @Enumerated(EnumType.STRING)
    private TypeRemboursement type;
    
    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;
}
