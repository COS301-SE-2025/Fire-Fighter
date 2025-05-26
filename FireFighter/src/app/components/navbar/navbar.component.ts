import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonHeader } from '@ionic/angular/standalone';
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

  constructor(private authService: AuthService) { }

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



