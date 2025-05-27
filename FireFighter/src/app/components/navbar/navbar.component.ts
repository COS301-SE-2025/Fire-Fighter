import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonHeader } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    IonHeader
  ]
})
export class NavbarComponent implements OnInit {
  mobileMenuOpen = false;
  profileMenuOpen = false;
  isDarkMode = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
    // Close profile menu when opening mobile menu
    if (this.mobileMenuOpen) {
      this.profileMenuOpen = false;
    }
  }

  toggleProfileMenu() {
    this.profileMenuOpen = !this.profileMenuOpen;
    // Close mobile menu when opening profile menu
    if (this.profileMenuOpen) {
      this.mobileMenuOpen = false;
    }
  }

  navigateToNotifications() {
    this.router.navigate(['/notifications']);
    this.closeMobileMenu();
  }

  navigateToDashboard() {
    this.router.navigate(['/dashboard']);
    this.closeMobileMenu();
  }

  navigateToRequests() {
    this.router.navigate(['/requests']);
    this.closeMobileMenu();
  }

  navigateToActivityLog() {
    // This will navigate to activity log when the route is implemented
    // For now, we can keep it as a placeholder or navigate to dashboard
    this.router.navigate(['/dashboard']);
    this.closeMobileMenu();
  }

  private closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

  ngOnInit(): void {
    const savedTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    this.isDarkMode = savedTheme === 'dark' || (!savedTheme && prefersDark);
    this.updateHtmlClass();
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    this.updateHtmlClass();
  }

  updateHtmlClass(): void {
    const html = document.documentElement;
    html.classList.toggle('dark', this.isDarkMode);
  }

  async logout() {
    await this.authService.logout();
  }
}



