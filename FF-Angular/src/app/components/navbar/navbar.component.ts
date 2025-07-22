import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonHeader } from '@ionic/angular/standalone';
import { Router, NavigationEnd} from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { ThemeService } from '../../services/theme.service';
import { Observable, map } from 'rxjs';

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

  currentURL = '';

  mobileMenuOpen = false;
  profileMenuOpen = false;
  isDarkMode = false;

  hasUnreadNotifications$: Observable<boolean>;
  isAdmin$: Observable<boolean>;

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private themeService: ThemeService
  ) {
    this.hasUnreadNotifications$ = this.notificationService.getNotifications().pipe(
      map(notifications => notifications.some(notification => !notification.read))
    );
    this.isAdmin$ = this.authService.isAdmin$;
  }

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

  navigateToHelp() {
    this.router.navigate(['/help']);
    this.closeMobileMenu();
    this.profileMenuOpen = false; // Close profile menu after navigation
  }

  navigateToAdmin() {
    this.router.navigate(['/admin']);
    this.closeMobileMenu();
    this.profileMenuOpen = false; // Close profile menu after navigation
  }

  navigateToChat() {
    this.router.navigate(['/chat']);
    this.closeMobileMenu();
  }

  navigateToAccount() {
    this.router.navigate(['/account']);
    this.closeMobileMenu();
    this.profileMenuOpen = false; // Close profile menu after navigation
  }

  navigateToSettings() {
    this.router.navigate(['/settings']);
    this.closeMobileMenu();
    this.profileMenuOpen = false; // Close profile menu after navigation
  }

  private closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

  ngOnInit(): void {
    // Get current theme from the theme service
    this.isDarkMode = this.themeService.getCurrentTheme();

    this.currentURL = this.router.url;
    this.router.events.subscribe(evt => {
      if (evt instanceof NavigationEnd) {
        this.currentURL = evt.urlAfterRedirects;
      }
    });
  }

  isActive(path: string): boolean {
    return this.currentURL === path;
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
    this.isDarkMode = this.themeService.getCurrentTheme();
  }



  async logout() {
    await this.authService.logout();
  }
}



