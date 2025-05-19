import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreditService } from '../../../core/services/credit.service';
import { Credit } from '../../../core/models/credit.model';
import { StatutCredit } from '../../../core/models/enums/statut-credit.enum';

@Component({
  selector: 'app-credit-detail',
  template: `
    <div class="min-h-screen bg-gray-100">
      <nav class="bg-white shadow-sm">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex justify-between h-16">
            <div class="flex">
              <div class="flex-shrink-0 flex items-center">
                <h1 class="text-xl font-bold text-indigo-600">Credit Management</h1>
              </div>
              <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
                <a routerLink="/dashboard" class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                  Dashboard
                </a>
                <a routerLink="/credits" class="border-indigo-500 text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                  Credits
                </a>
                <a routerLink="/clients" class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                  Clients
                </a>
                <a routerLink="/remboursements" class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                  Remboursements
                </a>
              </div>
            </div>
          </div>
        </div>
      </nav>

      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div class="px-4 py-6 sm:px-0">
          <div class="flex items-center mb-6">
            <button (click)="goBack()" class="mr-4 flex items-center text-sm text-indigo-600 hover:text-indigo-900">
              <svg class="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
              </svg>
              Back to Credits
            </button>
            <h2 class="text-2xl font-semibold text-gray-900">Credit Details</h2>
          </div>

          <div *ngIf="loading" class="text-center py-10">
            <svg class="animate-spin h-10 w-10 text-indigo-600 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-4 text-gray-600">Loading credit details...</p>
          </div>

          <div *ngIf="error" class="bg-red-50 p-4 rounded-md mb-6">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">Error loading credit details</h3>
                <div class="mt-2 text-sm text-red-700">
                  <p>{{ error }}</p>
                </div>
              </div>
            </div>
          </div>

          <div *ngIf="!loading && !error && credit" class="bg-white shadow overflow-hidden sm:rounded-lg">
            <div class="px-4 py-5 sm:px-6 flex justify-between items-center">
              <div>
                <h3 class="text-lg leading-6 font-medium text-gray-900">
                  {{ getCreditTypeLabel(credit.type) }} Credit #{{ credit.id }}
                </h3>
                <p class="mt-1 max-w-2xl text-sm text-gray-500">
                  Application date: {{ credit.dateDemande | date }}
                </p>
              </div>
              <div>
                <span [ngClass]="getStatusBadgeClass(credit.statut)" class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full">
                  {{ getStatusLabel(credit.statut) }}
                </span>
              </div>
            </div>
            <div class="border-t border-gray-200">
              <dl>
                <div class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">Amount</dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ credit.montant | currency:'EUR' }}</dd>
                </div>
                <div class="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">Duration</dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ credit.dureeRemboursement }} months</dd>
                </div>
                <div class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">Interest Rate</dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ credit.tauxInteret }}%</dd>
                </div>
                <div *ngIf="credit.client" class="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">Client</dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                    {{ credit.client.nom }} {{ credit.client.prenom }}
                  </dd>
                </div>
                
                <!-- Only show for PERSONNEL type -->
                <div *ngIf="credit.type === 'PERSONNEL' && credit.motif" class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">Motive</dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ credit.motif }}</dd>
                </div>
                
                <!-- Only show for IMMOBILIER type -->
                <div *ngIf="credit.type === 'IMMOBILIER' && credit.typeBienFinance" class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">Property Type</dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ credit.typeBienFinance }}</dd>
                </div>
                
                <!-- Only show for PROFESSIONNEL type -->
                <div *ngIf="credit.type === 'PROFESSIONNEL' && credit.raisonSocialeEntreprise" class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">Company Name</dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ credit.raisonSocialeEntreprise }}</dd>
                </div>
                
                <div *ngIf="credit.statut === 'ACCEPTE' && credit.dateAcception" class="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">Approval Date</dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ credit.dateAcception | date }}</dd>
                </div>
              </dl>
            </div>
            
            <div *ngIf="credit.statut === 'EN_COURS'" class="border-t border-gray-200 px-4 py-5 sm:px-6">
              <div class="flex justify-end space-x-3">
                <button (click)="approveCredit()" type="button" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500">
                  Approve
                </button>
                <button (click)="rejectCredit()" type="button" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500">
                  Reject
                </button>
              </div>
            </div>
          </div>

          <!-- Payment Schedule Section -->
          <div *ngIf="!loading && !error && credit && credit.statut === 'ACCEPTE'" class="mt-6">
            <h3 class="text-lg font-medium text-gray-900 mb-3">Payment Schedule</h3>
            
            <div *ngIf="loadingPayments" class="text-center py-6">
              <svg class="animate-spin h-6 w-6 text-indigo-600 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-gray-600">Loading payment schedule...</p>
            </div>
            
            <div *ngIf="!loadingPayments && payments.length > 0" class="bg-white shadow overflow-hidden sm:rounded-md">
              <ul role="list" class="divide-y divide-gray-200">
                <li *ngFor="let payment of payments; let i = index" class="px-4 py-4 sm:px-6">
                  <div class="flex items-center justify-between">
                    <div class="text-sm font-medium text-indigo-600">
                      Payment #{{ i + 1 }}
                    </div>
                    <div class="ml-2 flex-shrink-0 flex">
                      <p [ngClass]="payment.paid ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                        {{ payment.paid ? 'Paid' : 'Pending' }}
                      </p>
                    </div>
                  </div>
                  <div class="mt-2 sm:flex sm:justify-between">
                    <div class="sm:flex">
                      <p class="flex items-center text-sm text-gray-500">
                        <span>Amount: {{ payment.amount | currency:'EUR' }}</span>
                      </p>
                    </div>
                    <div class="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                      <p>
                        <time [dateTime]="payment.dueDate">Due: {{ payment.dueDate | date }}</time>
                      </p>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
            
            <div *ngIf="!loadingPayments && payments.length === 0" class="bg-white px-4 py-5 border-b border-gray-200 sm:px-6 rounded-md text-center">
              <p class="text-gray-500">No payment schedule available</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule]
})
export class CreditDetailComponent implements OnInit {
  credit: Credit | null = null;
  loading = true;
  error: string | null = null;
  
  payments: any[] = [];
  loadingPayments = false;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private creditService: CreditService
  ) {}

  ngOnInit(): void {
    this.loadCreditDetails();
  }

  loadCreditDetails(): void {
    this.loading = true;
    this.error = null;
    
    const id = Number(this.route.snapshot.paramMap.get('id'));
    
    if (isNaN(id)) {
      this.error = 'Invalid credit ID';
      this.loading = false;
      return;
    }
    
    this.creditService.getCreditById(id).subscribe({
      next: (data: Credit) => {
        this.credit = data;
        this.loading = false;
        
        if (data.statut === StatutCredit.ACCEPTE) {
          this.loadPaymentSchedule(id);
        }
      },
      error: (err: any) => {
        console.error('Error loading credit details', err);
        this.error = err.message || 'An unexpected error occurred';
        this.loading = false;
      }
    });
  }

  loadPaymentSchedule(id: number): void {
    this.loadingPayments = true;
    
    this.creditService.getPaymentSchedule(id).subscribe({
      next: (data: any[]) => {
        this.payments = data;
        this.loadingPayments = false;
      },
      error: (err: any) => {
        console.error('Error loading payment schedule', err);
        this.loadingPayments = false;
      }
    });
  }

  approveCredit(): void {
    if (!this.credit || this.credit.statut !== StatutCredit.EN_COURS) {
      return;
    }
    
    if (!confirm('Are you sure you want to approve this credit?')) {
      return;
    }
    
    const today = new Date();
    
    this.creditService.approveCredit(this.credit.id, today).subscribe({
      next: (updatedCredit: Credit) => {
        this.credit = updatedCredit;
        alert('Credit approved successfully');
        
        // Load payment schedule after approval
        this.loadPaymentSchedule(updatedCredit.id);
      },
      error: (err: any) => {
        console.error('Error approving credit', err);
        alert('Error approving credit: ' + (err.message || 'An unexpected error occurred'));
      }
    });
  }

  rejectCredit(): void {
    if (!this.credit || this.credit.statut !== StatutCredit.EN_COURS) {
      return;
    }
    
    const reason = prompt('Please enter a reason for rejection (optional):');
    
    this.creditService.rejectCredit(this.credit.id, reason || '').subscribe({
      next: (updatedCredit: Credit) => {
        this.credit = updatedCredit;
        alert('Credit rejected successfully');
      },
      error: (err: any) => {
        console.error('Error rejecting credit', err);
        alert('Error rejecting credit: ' + (err.message || 'An unexpected error occurred'));
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/credits']);
  }

  getStatusBadgeClass(status: string): string {
    switch(status) {
      case StatutCredit.ACCEPTE:
        return 'bg-green-100 text-green-800';
      case StatutCredit.REJETE:
        return 'bg-red-100 text-red-800';
      case StatutCredit.EN_COURS:
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getStatusLabel(status: string): string {
    switch(status) {
      case StatutCredit.ACCEPTE:
        return 'Approved';
      case StatutCredit.REJETE:
        return 'Rejected';
      case StatutCredit.EN_COURS:
        return 'Pending';
      default:
        return status;
    }
  }

  getCreditTypeLabel(type: string | undefined): string {
    if (!type) return 'Unknown';
    
    switch(type) {
      case 'PERSONNEL':
        return 'Personal';
      case 'IMMOBILIER':
        return 'Real Estate';
      case 'PROFESSIONNEL':
        return 'Professional';
      default:
        return type;
    }
  }
}
