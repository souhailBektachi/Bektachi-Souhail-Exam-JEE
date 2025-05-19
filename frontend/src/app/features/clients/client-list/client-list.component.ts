import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Client } from '../client.model';
import { ClientService } from '../client.service';
// HttpClientModule might be needed here if ClientService is not providedIn: 'root'
// or if this component makes direct HTTP calls, but it's better practice to use the service.

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, RouterModule], // RouterModule if you add navigation links like "Add New"
  template: `
    <h2>Clients</h2>
    <button (click)="addClient()">Add New Client</button>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Email</th>
          <th>Credits</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let client of clients">
          <td>{{ client.id }}</td>
          <td>{{ client.nom }}</td>
          <td>{{ client.email }}</td>
          <td>{{ client.nombreCredits }}</td>
          <td>
            <button (click)="editClient(client.id)">Edit</button>
            <button (click)="deleteClient(client.id)">Delete</button>
          </td>
        </tr>
        <tr *ngIf="!clients || clients.length === 0">
          <td colspan="5">No clients found.</td>
        </tr>
      </tbody>
    </table>
    <p *ngIf="errorMessage" style="color: red;">{{ errorMessage }}</p>
  `,
  styles: [`
    table { width: 100%; border-collapse: collapse; margin-top: 1em; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    button { margin-right: 5px; }
  `]
})
export class ClientListComponent implements OnInit {
  clients: Client[] = [];
  errorMessage: string | null = null;

  constructor(private clientService: ClientService) { }

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.errorMessage = null;
    this.clientService.getClients().subscribe({
      next: (data) => {
        this.clients = data;
      },
      error: (err) => {
        console.error('Error fetching clients', err);
        this.errorMessage = 'Failed to load clients. Please try again later.';
        this.clients = []; // Ensure clients is empty on error
      }
    });
  }

  addClient(): void {
    // Navigate to an add client form/modal or implement inline adding
    console.log('Add new client clicked');
    // Example: this.router.navigate(['/clients/new']);
    alert('Add client functionality not yet implemented.');
  }

  editClient(id: number): void {
    // Navigate to an edit client form/modal
    console.log('Edit client clicked for id:', id);
    // Example: this.router.navigate(['/clients/edit', id]);
    alert('Edit client functionality not yet implemented.');
  }

  deleteClient(id: number): void {
    if (confirm('Are you sure you want to delete this client?')) {
      this.errorMessage = null;
      this.clientService.deleteClient(id).subscribe({
        next: () => {
          console.log('Client deleted successfully');
          this.loadClients(); // Refresh the list
        },
        error: (err) => {
          console.error('Error deleting client', err);
          this.errorMessage = 'Failed to delete client.';
        }
      });
    }
  }
}
