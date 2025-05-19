import { Routes } from '@angular/router';
import { ClientListComponent } from './client-list/client-list.component';
// Import other components like ClientFormComponent, ClientDetailComponent when you create them

export const CLIENT_ROUTES: Routes = [
  {
    path: '',
    component: ClientListComponent
  },
  // Example for future routes:
  // {
  //   path: 'new',
  //   component: ClientFormComponent // A component for adding new clients
  // },
  // {
  //   path: 'edit/:id',
  //   component: ClientFormComponent // A component for editing existing clients
  // },
  // {
  //   path: ':id',
  //   component: ClientDetailComponent // A component for viewing client details
  // }
];
