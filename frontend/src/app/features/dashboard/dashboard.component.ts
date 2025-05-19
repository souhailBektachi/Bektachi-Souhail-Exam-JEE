import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common'; // For *ngIf, *ngFor, etc.
import { AuthService } from '../../core/services/auth.service'; // Import AuthService

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
    <div class="dashboard-container">
      <h1>Dashboard</h1>
      <button (click)="logout()">Logout</button>
      <div *ngIf="summary; else loading" class="summary-cards">
        <div class="card">
          <h2>Total Clients</h2>
          <p>{{ summary.totalClients }}</p>
        </div>
        <div class="card">
          <h2>Total Credits</h2>
          <p>{{ summary.totalCredits }}</p>
        </div>
        <div class="card">
          <h2>Pending Credits</h2>
          <p>{{ summary.pendingCredits }}</p>
        </div>
        <div class="card">
          <h2>Approved Credits</h2>
          <p>{{ summary.approvedCredits }}</p>
        </div>
        <div class="card">
          <h2>Rejected Credits</h2>
          <p>{{ summary.rejectedCredits }}</p>
        </div>
      </div>
      <ng-template #loading>
        <p>Loading dashboard data...</p>
      </ng-template>
      <div *ngIf="error" class="error-message">
        <p>Error loading dashboard data: {{ error }}</p>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container { padding: 20px; }
    .summary-cards { display: flex; flex-wrap: wrap; gap: 20px; margin-top: 20px; }
    .card { border: 1px solid #ccc; border-radius: 8px; padding: 20px; min-width: 200px; box-shadow: 2px 2px 5px rgba(0,0,0,0.1); }
    .card h2 { margin-top: 0; }
    .error-message { color: red; margin-top: 20px; }
  `],
  standalone: true,
  imports: [CommonModule, HttpClientModule]
})
export class DashboardComponent implements OnInit {
  summary: DashboardSummary | null = null;
  error: string | null = null;

  constructor(private http: HttpClient, private authService: AuthService) {}

  ngOnInit(): void {
    this.fetchDashboardSummary();
  }

  fetchDashboardSummary(): void {
    this.http.get<DashboardSummary>('/api/dashboard/summary').subscribe({
      next: (data) => {
        this.summary = data;
        this.error = null;
      },
      error: (err) => {
        console.error('Failed to fetch dashboard summary', err);
        this.error = err.message || 'Unknown error';
        this.summary = null; // Clear summary on error
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
