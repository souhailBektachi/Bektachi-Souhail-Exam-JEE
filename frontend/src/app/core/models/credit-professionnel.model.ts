import { Credit } from "./credit.model";

export interface CreditProfessionnel extends Credit {
  motif: string;
  raisonSocialeEntreprise: string;
}
