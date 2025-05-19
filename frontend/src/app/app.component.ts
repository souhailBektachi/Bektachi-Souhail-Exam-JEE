import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'Credit Management System';
  
  constructor(private authService: AuthService) {}
  
  ngOnInit() {
    // Check if the user is already authenticated (from previous session)
    this.authService.checkAuthState();
  }
}
