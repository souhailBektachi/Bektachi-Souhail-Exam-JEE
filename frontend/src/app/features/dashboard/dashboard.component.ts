import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CreditService } from '../../core/services/credit.service';
import { StatutCredit } from '../../core/models/enums/statut-credit.enum';
import { CreditSummary } from '../../core/models/credit-summary.model';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

interface DashboardSummary {
  totalClients: number;
  totalCredits: number;
  pendingCredits: number;
  approvedCredits: number;
  rejectedCredits: number;
}

@Component({
  selector: 'app-dashboard',
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
                <a routerLink="/dashboard" class="border-indigo-500 text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                  Dashboard
                </a>
                <a routerLink="/credits" class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
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
            <div class="flex items-center">
              <div class="ml-3 relative">
                <div>
                  <button (click)="logout()" class="text-gray-500 hover:text-gray-700 bg-white hover:bg-gray-50 border border-gray-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 font-medium rounded-md text-sm px-4 py-2">
                    Logout
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </nav>

      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div class="px-4 py-6 sm:px-0">
          <h2 class="text-2xl font-semibold text-gray-900 mb-6">Dashboard</h2>

          <div *ngIf="isLoading" class="text-center py-10">
            <svg class="animate-spin h-10 w-10 text-indigo-600 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-4 text-gray-600">Loading dashboard data...</p>
          </div>

          <div *ngIf="error" class="bg-red-50 p-4 rounded-md mb-6">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">Error loading dashboard data</h3>
                <div class="mt-2 text-sm text-red-700">
                  <p>{{ error }}</p>
                </div>
              </div>
            </div>
          </div>

          <div *ngIf="!isLoading && !error" class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
            <div class="bg-white overflow-hidden shadow rounded-lg">
              <div class="px-4 py-5 sm:p-6">
                <div class="flex items-center">
                  <div class="flex-shrink-0 bg-indigo-500 rounded-md p-3">
                    <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                  </div>
                  <div class="ml-5 w-0 flex-1">
                    <dl>
                      <dt class="text-sm font-medium text-gray-500 truncate">Total Credits</dt>
                      <dd class="text-3xl font-semibold text-gray-900">{{ creditCount }}</dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>

            <div *ngFor="let status of creditStatusSummary | keyvalue" class="bg-white overflow-hidden shadow rounded-lg">
              <div class="px-4 py-5 sm:p-6">
                <div class="flex items-center">
                  <div [ngClass]="getStatusColor(status.key)" class="flex-shrink-0 rounded-md p-3">
                    <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                  </div>
                  <div class="ml-5 w-0 flex-1">
                    <dl>
                      <dt class="text-sm font-medium text-gray-500 truncate">{{ formatStatusLabel(status.key) }} Credits</dt>
                      <dd class="text-3xl font-semibold text-gray-900">{{ status.value }}</dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-8">
            <div class="flex justify-between items-center mb-4">
              <h3 class="text-lg font-medium text-gray-900">Recent Credits</h3>
              <a routerLink="/credits" class="text-sm text-indigo-600 hover:text-indigo-900">View all</a>
            </div>
            <div *ngIf="!isLoading && latestCredits.length > 0" class="bg-white shadow overflow-hidden sm:rounded-md">
              <ul role="list" class="divide-y divide-gray-200">
                <li *ngFor="let credit of latestCredits">
                  <a [routerLink]="['/credits', credit.id]" class="block hover:bg-gray-50">
                    <div class="px-4 py-4 sm:px-6">
                      <div class="flex items-center justify-between">
                        <p class="text-sm font-medium text-indigo-600 truncate">{{ credit.type }} Credit</p>
                        <div class="ml-2 flex-shrink-0 flex">
                          <p [ngClass]="getStatusBadgeClass(credit.statut)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                            {{ credit.statut }}
                          </p>
                        </div>
                      </div>
                      <div class="mt-2 sm:flex sm:justify-between">
                        <div class="sm:flex">
                          <p class="flex items-center text-sm text-gray-500">
                            <span class="truncate">{{ credit.montant | currency:'EUR' }}</span>
                          </p>
                          <p class="mt-2 flex items-center text-sm text-gray-500 sm:mt-0 sm:ml-6">
                            <span class="truncate">{{ credit.dureeRemboursement }} months</span>
                          </p>
                        </div>
                        <div class="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                          <p>
                            <time [dateTime]="credit.dateDemande">{{ credit.dateDemande | date }}</time>
                          </p>
                        </div>
                      </div>
                    </div>
                  </a>
                </li>
              </ul>
            </div>
            <div *ngIf="!isLoading && latestCredits.length === 0" class="bg-white px-4 py-5 border-b border-gray-200 sm:px-6 rounded-md text-center">
              <p class="text-gray-500">No credits found</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class DashboardComponent implements OnInit {
  isLoading = true;
  error: string | null = null;
  creditCount: number = 0;
  creditStatusSummary: Map<StatutCredit, number> = new Map();
  latestCredits: any[] = [];

  constructor(
    private authService: AuthService,
    private creditService: CreditService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchDashboardData();
  }
  fetchDashboardData(): void {
    this.isLoading = true;
    this.error = null;

    // Get credit count
    this.creditService.countCredits().subscribe({
      next: (count: number) => {
        this.creditCount = count;
        this.fetchStatusSummary();
      },
      error: (err: any) => this.handleError(err)
    });
  }

  fetchStatusSummary(): void {
    this.creditService.getCreditStatusSummary().subscribe({
      next: (summary: Map<StatutCredit, number>) => {
        this.creditStatusSummary = summary;
        this.fetchLatestCredits();
      },
      error: (err: any) => this.handleError(err)
    });
  }
  fetchLatestCredits(): void {
    this.creditService.getAllCredits().subscribe({
      next: (credits: CreditSummary[]) => {
        this.latestCredits = credits.slice(0, 5); // Take just the first 5 credits
        this.isLoading = false;
      },
      error: (err: any) => this.handleError(err)
    });
  }

  handleError(err: any): void {
    console.error('Error fetching dashboard data', err);
    
    if (err instanceof HttpErrorResponse) {
      if (err.status === 403) {
        this.error = 'You are not authorized to access this information. Please log in again.';
        // Redirect to login after a brief delay to allow the user to see the error
        setTimeout(() => {
          this.authService.logout();
          this.router.navigate(['/login']);
        }, 3000);
      } else if (err.status === 401) {
        this.error = 'Your session has expired. Please log in again.';
        this.authService.logout();
        this.router.navigate(['/login']);
      } else {
        this.error = `Server error: ${err.message || 'An unexpected error occurred'}`;
      }
    } else {
      this.error = err.message || 'An unexpected error occurred';
    }
    
    this.isLoading = false;
  }

  getStatusColor(status: string): string {
    switch(status) {
      case StatutCredit.ACCEPTE:
        return 'bg-green-500';
      case StatutCredit.REJETE:
        return 'bg-red-500';
      case StatutCredit.EN_COURS:
        return 'bg-yellow-500';
      default:
        return 'bg-gray-500';
    }
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

  logout(): void {
    this.authService.logout();
  }
}
