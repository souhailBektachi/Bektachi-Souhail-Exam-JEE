package com.souhailbektachi.backend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreditPersonnelRequestDTO extends CreditRequestDTO {
    private String motif;
}
