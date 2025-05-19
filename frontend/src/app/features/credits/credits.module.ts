import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./credit-list/credit-list.component').then(
        (m) => m.CreditListComponent
      ),
  },
  {
    path: 'create',
    loadComponent: () =>
      import('./credit-form/credit-form.component').then(
        (m) => m.CreditFormComponent
      ),
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./credit-detail/credit-detail.component').then(
        (m) => m.CreditDetailComponent
      ),
  },
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
  ],
})
export class CreditsModule {}
