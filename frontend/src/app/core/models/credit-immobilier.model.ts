import { Credit } from "./credit.model";
import { TypeBienImmobilier } from "./enums/type-bien-immobilier.enum";

export interface CreditImmobilier extends Credit {
  typeBienFinance: TypeBienImmobilier;
}
