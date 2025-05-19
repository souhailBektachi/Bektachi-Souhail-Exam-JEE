import { Client } from "../../../app/features/clients/client.model";
import { Remboursement } from "./remboursement.model";
import { StatutCredit } from "./enums/statut-credit.enum";

export interface Credit {
  id: number;
  dateDemande: string; // Consider using Date type if you parse it
  statut: StatutCredit;
  dateAcception?: string; // Consider using Date type
  montant: number;
  dureeRemboursement: number;
  tauxInteret: number;
  client?: Client;
  remboursements?: Remboursement[];
  type?: string; // "PERSONNEL", "IMMOBILIER", "PROFESSIONNEL"
  
  // Fields for specific credit types
  motif?: string; // For PERSONNEL credits
  typeBienFinance?: string; // For IMMOBILIER credits
  raisonSocialeEntreprise?: string; // For PROFESSIONNEL credits
}
