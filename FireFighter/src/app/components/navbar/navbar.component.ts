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

  constructor(private authService: AuthService) { }

  ngOnInit() {}

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

  async logout() {
    await this.authService.logout();
  }
}
