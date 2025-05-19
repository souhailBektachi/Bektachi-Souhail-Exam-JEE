import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportingService {
  private apiUrl = `${environment.apiUrl}/reporting`;

  constructor(private http: HttpClient) { }

  getCreditSummaryByStatus(): Observable<any> { // Define a more specific type for the response
    return this.http.get<any>(`${this.apiUrl}/credits/summary-by-status`);
  }

  getCreditSummaryByType(): Observable<any> { // Define a more specific type for the response
    return this.http.get<any>(`${this.apiUrl}/credits/summary-by-type`);
  }

  // Add other reporting methods if they exist in the backend
}
