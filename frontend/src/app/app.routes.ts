import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'clients',
    loadChildren: () => import('./features/clients/client.routes').then(m => m.CLIENT_ROUTES)
    // canActivate: [AuthGuard] // Example: If you have an AuthGuard
  },
  {
    path: '',
    redirectTo: '/auth/login', // Or '/clients' if you want to default to client list after login
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/auth/login' // Or a 404 component
  }
];
