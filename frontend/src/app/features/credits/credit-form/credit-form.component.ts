import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, FormControl } from '@angular/forms';
import { CreditService } from '../../../core/services/credit.service';
import { ClientService } from '../../clients/client.service';
import { Client } from '../../clients/client.model';

// Credit request interface for type safety
interface CreditRequest {
  type: string;
  clientId: number;
  montant: number;
  dureeRemboursement: number;
  motif?: string;
  typeBienFinance?: string;
  raisonSocialeEntreprise?: string;
}

@Component({
  selector: 'app-credit-form',
  templateUrl: './credit-form.component.html',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule]
})
export class CreditFormComponent implements OnInit {
  creditForm: FormGroup;
  clients: Client[] = [];
  submitted = false;
  error: string | null = null;
  selectedCreditType: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private creditService: CreditService,
    private clientService: ClientService
  ) {
    this.creditForm = this.formBuilder.group({
      type: ['', Validators.required],
      clientId: ['', Validators.required],
      montant: [0, [Validators.required, Validators.min(1)]],
      dureeRemboursement: [12, [Validators.required, Validators.min(1), Validators.max(360)]],
      // Optional fields for specific credit types
      motif: [''],
      typeBienFinance: [''],
      raisonSocialeEntreprise: ['']
    });
  }

  ngOnInit(): void {
    // Fetch clients from the API
    this.clientService.getClients().subscribe({
      next: (clients) => {
        this.clients = clients;
      },
      error: (err) => {
        console.error('Error fetching clients', err);
        
        // Fallback to sample data if API fails
        this.clients = [
          { id: 1, nom: 'Doe', prenom: 'John', email: 'john.doe@example.com', nombreCredits: 2 },
          { id: 2, nom: 'Smith', prenom: 'Jane', email: 'jane.smith@example.com', nombreCredits: 1 },
          { id: 3, nom: 'Brown', prenom: 'Robert', email: 'robert.brown@example.com', nombreCredits: 0 }
        ];
      }
    });
  }

  get f(): { [key: string]: AbstractControl } { 
    return this.creditForm.controls; 
  }

  onCreditTypeChange(): void {
    const typeControl = this.creditForm.get('type');
    this.selectedCreditType = typeControl?.value || '';
    
    // Reset type-specific fields
    const motifControl = this.creditForm.get('motif');
    const typeBienFinanceControl = this.creditForm.get('typeBienFinance');
    const raisonSocialeEntrepriseControl = this.creditForm.get('raisonSocialeEntreprise');
    
    if (motifControl) motifControl.clearValidators();
    if (typeBienFinanceControl) typeBienFinanceControl.clearValidators();
    if (raisonSocialeEntrepriseControl) raisonSocialeEntrepriseControl.clearValidators();
    
    // Set validators based on selected type
    if (this.selectedCreditType === 'PERSONNEL' && motifControl) {
      motifControl.setValidators([Validators.required]);
    } else if (this.selectedCreditType === 'IMMOBILIER' && typeBienFinanceControl) {
      typeBienFinanceControl.setValidators([Validators.required]);
    } else if (this.selectedCreditType === 'PROFESSIONNEL' && raisonSocialeEntrepriseControl) {
      raisonSocialeEntrepriseControl.setValidators([Validators.required]);
    }
    
    // Update validation status
    if (motifControl) motifControl.updateValueAndValidity();
    if (typeBienFinanceControl) typeBienFinanceControl.updateValueAndValidity();
    if (raisonSocialeEntrepriseControl) raisonSocialeEntrepriseControl.updateValueAndValidity();
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;
    
    if (this.creditForm.invalid) {
      return;
    }
    
    const formData = this.creditForm.value;
    
    // Create a credit request based on the form data
    const creditRequest: CreditRequest = {
      type: formData.type,
      clientId: parseInt(formData.clientId, 10),
      montant: formData.montant,
      dureeRemboursement: formData.dureeRemboursement
    };
    
    // Add type-specific fields based on the selected credit type
    if (formData.type === 'PERSONNEL' && formData.motif) {
      creditRequest.motif = formData.motif;
    } else if (formData.type === 'IMMOBILIER' && formData.typeBienFinance) {
      creditRequest.typeBienFinance = formData.typeBienFinance;
    } else if (formData.type === 'PROFESSIONNEL' && formData.raisonSocialeEntreprise) {
      creditRequest.raisonSocialeEntreprise = formData.raisonSocialeEntreprise;
    }
    
    this.creditService.createCredit(creditRequest).subscribe({
      next: (response) => {
        // Navigate to the credit detail page for the newly created credit
        this.router.navigate(['/credits', response.id]);
      },
      error: (err: any) => {
        console.error('Error creating credit', err);
        this.error = err.message || 'An error occurred while creating the credit. Please try again.';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/credits']);
  }
}
