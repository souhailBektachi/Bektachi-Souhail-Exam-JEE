import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'clients',
    loadChildren: () => import('./features/clients/client.routes').then(m => m.CLIENT_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'credits',
    loadChildren: () => import('./features/credits/credits.module').then(m => m.CreditsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'remboursements',
    loadChildren: () => import('./features/remboursements/remboursements.module').then(m => m.RemboursementsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.module').then(m => m.AdminModule),
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/auth/login'
  }
];
