package com.souhailbektachi.backend.dtos;

import com.souhailbektachi.backend.entities.TypeBienImmobilier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreditImmobilierRequestDTO extends CreditRequestDTO {
    private TypeBienImmobilier typeBienFinance;
}
