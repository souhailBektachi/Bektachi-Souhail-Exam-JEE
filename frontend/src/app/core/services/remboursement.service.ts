import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Remboursement } from '../models/remboursement.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RemboursementService {
  private apiUrl = `${environment.apiUrl}/remboursements`;

  constructor(private http: HttpClient) { }

  getAllRemboursements(): Observable<Remboursement[]> {
    return this.http.get<Remboursement[]>(this.apiUrl);
  }

  getRemboursementById(id: number): Observable<Remboursement> {
    return this.http.get<Remboursement>(`${this.apiUrl}/${id}`);
  }

  createRemboursement(remboursementData: any): Observable<Remboursement> { // Use RemboursementRequest model
    return this.http.post<Remboursement>(this.apiUrl, remboursementData);
  }

  // Add other methods as needed based on backend RemboursementController
}
