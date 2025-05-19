import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CreditSummary } from '../models/credit-summary.model';
import { Credit } from '../models/credit.model';
import { environment } from '../../../environments/environment';
import { StatutCredit } from '../models/enums/statut-credit.enum';

@Injectable({
  providedIn: 'root'
})
export class CreditService {
  private apiUrl = `${environment.apiUrl}/api/credits`;

  constructor(private http: HttpClient) { }

  getAllCredits(): Observable<CreditSummary[]> {
    return this.http.get<CreditSummary[]>(this.apiUrl);
  }

  getCreditById(id: number): Observable<Credit> {
    return this.http.get<Credit>(`${this.apiUrl}/${id}`);
  }

  createCredit(creditData: any): Observable<Credit> { // Use a more specific type e.g. CreditRequest
    return this.http.post<Credit>(this.apiUrl, creditData);
  }

  updateCredit(id: number, creditData: any): Observable<Credit> {
    return this.http.put<Credit>(`${this.apiUrl}/${id}`, creditData);
  }

  updateCreditStatus(id: number, statut: string): Observable<Credit> {
    return this.http.patch<Credit>(`${this.apiUrl}/${id}/statut`, { statut });
  }

  deleteCredit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getCreditsByStatus(status: StatutCredit): Observable<CreditSummary[]> {
    return this.http.get<CreditSummary[]>(`${this.apiUrl}/status/${status}`);
  }

  getCreditsByType(type: string): Observable<CreditSummary[]> {
    return this.http.get<CreditSummary[]>(`${this.apiUrl}/type/${type}`);
  }

  approveCredit(id: number, approvalDate: Date): Observable<Credit> {
    return this.http.post<Credit>(`${this.apiUrl}/${id}/approve`, { approvalDate });
  }

  rejectCredit(id: number, reason: string): Observable<Credit> {
    return this.http.post<Credit>(`${this.apiUrl}/${id}/reject`, { reason });
  }

  calculateMonthlyPayment(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/payment-calculation`);
  }

  getPaymentSchedule(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/payment-schedule`);
  }

  validateCreditApplication(creditData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/validate`, creditData);
  }

  searchCreditsByAmountRange(minAmount: number, maxAmount: number): Observable<CreditSummary[]> {
    return this.http.get<CreditSummary[]>(`${this.apiUrl}/search/amount`, {
      params: {
        minAmount: minAmount.toString(),
        maxAmount: maxAmount.toString()
      }
    });
  }

  searchCreditsByDateRange(startDate: Date, endDate: Date): Observable<CreditSummary[]> {
    return this.http.get<CreditSummary[]>(`${this.apiUrl}/search/date`, {
      params: {
        startDate: startDate.toISOString().split('T')[0],
        endDate: endDate.toISOString().split('T')[0]
      }
    });
  }
  countCredits(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count`)
      .pipe(
        catchError(error => {
          console.error('Error counting credits:', error);
          return throwError(() => new Error(`Failed to count credits: ${error.message}`));
        })
      );
  }

  getCreditStatusSummary(): Observable<Map<StatutCredit, number>> {
    return this.http.get<Map<StatutCredit, number>>(`${this.apiUrl}/status-summary`)
      .pipe(
        catchError(error => {
          console.error('Error getting credit status summary:', error);
          return throwError(() => new Error(`Failed to get credit status summary: ${error.message}`));
        })
      );
  }
}
