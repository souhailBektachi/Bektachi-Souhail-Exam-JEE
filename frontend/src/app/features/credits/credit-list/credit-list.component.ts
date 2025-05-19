import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CreditService } from '../../../core/services/credit.service';
import { CreditSummary } from '../../../core/models/credit-summary.model';
import { StatutCredit } from '../../../core/models/enums/statut-credit.enum';

@Component({
  selector: 'app-credit-list',
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
          <div class="mb-6 flex flex-col md:flex-row md:items-center md:justify-between">
            <h2 class="text-2xl font-semibold text-gray-900">Credit Management</h2>
            <div class="mt-4 md:mt-0 flex flex-col sm:flex-row sm:space-x-3">
              <button (click)="openCreateCreditModal()" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 mb-2 sm:mb-0">
                <svg class="-ml-1 mr-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
                </svg>
                New Credit
              </button>
              
              <div class="relative">
                <select [(ngModel)]="selectedStatus" (change)="filterByStatus()" class="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
                  <option [ngValue]="null">All Statuses</option>
                  <option [ngValue]="StatutCredit.EN_COURS">Pending</option>
                  <option [ngValue]="StatutCredit.ACCEPTE">Approved</option>
                  <option [ngValue]="StatutCredit.REJETE">Rejected</option>
                </select>
              </div>
              
              <div class="relative mt-2 sm:mt-0">
                <select [(ngModel)]="selectedType" (change)="filterByType()" class="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
                  <option [ngValue]="null">All Types</option>
                  <option value="PERSONNEL">Personal</option>
                  <option value="IMMOBILIER">Real Estate</option>
                  <option value="PROFESSIONNEL">Professional</option>
                </select>
              </div>
            </div>
          </div>

          <div *ngIf="loading" class="text-center py-10">
            <svg class="animate-spin h-10 w-10 text-indigo-600 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-4 text-gray-600">Loading credits...</p>
          </div>

          <div *ngIf="error" class="bg-red-50 p-4 rounded-md mb-6">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">Error loading credits</h3>
                <div class="mt-2 text-sm text-red-700">
                  <p>{{ error }}</p>
                </div>
              </div>
            </div>
          </div>

          <div *ngIf="!loading && !error && credits.length === 0" class="bg-white px-4 py-5 border-b border-gray-200 sm:px-6 rounded-md text-center">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path vector-effect="non-scaling-stroke" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 13h6m-3-3v6m-9 1V7a2 2 0 012-2h6l2 2h6a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
            </svg>
            <h3 class="mt-2 text-sm font-medium text-gray-900">No credits found</h3>
            <p class="mt-1 text-sm text-gray-500">Get started by creating a new credit application.</p>
            <div class="mt-6">
              <button (click)="openCreateCreditModal()" type="button" class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                <svg class="-ml-1 mr-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
                </svg>
                New Credit
              </button>
            </div>
          </div>

          <div *ngIf="!loading && !error && credits.length > 0" class="flex flex-col">
            <div class="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
              <div class="py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8">
                <div class="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
                  <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                      <tr>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Duration</th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Application Date</th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                        <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                      </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                      <tr *ngFor="let credit of credits">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ credit.id }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ credit.type }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ credit.montant | currency:'EUR' }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ credit.dureeRemboursement }} months</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ credit.dateDemande | date }}</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                          <span [ngClass]="getStatusBadgeClass(credit.statut)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                            {{ formatStatusLabel(credit.statut) }}
                          </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <a [routerLink]="['/credits', credit.id]" class="text-indigo-600 hover:text-indigo-900 mr-3">View</a>
                          <a *ngIf="credit.statut === StatutCredit.EN_COURS" (click)="approveCredit(credit.id)" class="text-green-600 hover:text-green-900 mr-3 cursor-pointer">Approve</a>
                          <a *ngIf="credit.statut === StatutCredit.EN_COURS" (click)="rejectCredit(credit.id)" class="text-red-600 hover:text-red-900 cursor-pointer">Reject</a>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class CreditListComponent implements OnInit {
  credits: CreditSummary[] = [];
  loading = true;
  error: string | null = null;
  selectedStatus: StatutCredit | null = null;
  selectedType: string | null = null;
  StatutCredit = StatutCredit; // Make enum available to template

  constructor(
    private creditService: CreditService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCredits();
  }

  loadCredits(): void {
    this.loading = true;
    this.error = null;
    
    this.creditService.getAllCredits().subscribe({
      next: (data: CreditSummary[]) => {
        this.credits = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading credits', err);
        this.error = err.message || 'An unexpected error occurred';
        this.loading = false;
      }
    });
  }

  filterByStatus(): void {
    this.loading = true;
    
    if (this.selectedStatus === null) {
      this.loadCredits();
      return;
    }
    
    this.creditService.getCreditsByStatus(this.selectedStatus).subscribe({
      next: (data: CreditSummary[]) => {
        this.credits = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error filtering credits by status', err);
        this.error = err.message || 'An unexpected error occurred';
        this.loading = false;
      }
    });
  }

  filterByType(): void {
    this.loading = true;
    
    if (this.selectedType === null) {
      this.loadCredits();
      return;
    }
    
    this.creditService.getCreditsByType(this.selectedType).subscribe({
      next: (data: CreditSummary[]) => {
        this.credits = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error filtering credits by type', err);
        this.error = err.message || 'An unexpected error occurred';
        this.loading = false;
      }
    });
  }

  approveCredit(id: number): void {
    if (!confirm('Are you sure you want to approve this credit?')) {
      return;
    }
    
    const today = new Date();
    
    this.creditService.approveCredit(id, today).subscribe({
      next: () => {
        alert('Credit approved successfully');
        this.loadCredits();
      },
      error: (err: any) => {
        console.error('Error approving credit', err);
        alert('Error approving credit: ' + (err.message || 'An unexpected error occurred'));
      }
    });
  }

  rejectCredit(id: number): void {
    const reason = prompt('Please enter a reason for rejection (optional):');
    
    this.creditService.rejectCredit(id, reason || '').subscribe({
      next: () => {
        alert('Credit rejected successfully');
        this.loadCredits();
      },
      error: (err: any) => {
        console.error('Error rejecting credit', err);
        alert('Error rejecting credit: ' + (err.message || 'An unexpected error occurred'));
      }
    });
  }
  openCreateCreditModal(): void {
    this.router.navigate(['/credits/create']);
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

  formatStatusLabel(status: string): string {
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
}
