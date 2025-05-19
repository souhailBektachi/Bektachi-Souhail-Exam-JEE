import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 text-center">
        <div>
          <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">404</h2>
          <p class="mt-2 text-center text-sm text-gray-600">
            Page not found
          </p>
        </div>
        <div class="mt-8">
          <p class="text-gray-600 mb-4">The page you're looking for doesn't exist or has been moved.</p>
          <a routerLink="/" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary">
            Go back home
          </a>
        </div>
      </div>
    </div>
  `,
})
export class NotFoundComponent {}
