import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'dashboard',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES)
  },
  {
    path: 'clients',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_EMPLOYE'] },
    loadChildren: () => import('./features/clients/clients.routes').then(m => m.CLIENTS_ROUTES)
  },
  {
    path: 'credits',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/credits/credits.routes').then(m => m.CREDITS_ROUTES)
  },
  {
    path: 'repayments',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/repayments/repayments.routes').then(m => m.REPAYMENTS_ROUTES)
  },
  {
    path: 'reports',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_EMPLOYE'] },
    loadChildren: () => import('./features/reports/reports.routes').then(m => m.REPORTS_ROUTES)
  },
  {
    path: 'profile',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/profile/profile.routes').then(m => m.PROFILE_ROUTES)
  },
  {
    path: '**',
    loadComponent: () => import('./core/components/not-found/not-found.component').then(c => c.NotFoundComponent)
  }
];
