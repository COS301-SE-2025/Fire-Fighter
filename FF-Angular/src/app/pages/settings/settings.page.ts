import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { VersionService } from '../../services/version.service';
import { LanguageService, Language } from '../../services/language.service';
import { TranslateModule } from '@ngx-translate/core';
import { UserPreferencesService, NotificationSettings, UserPreferences } from '../../services/user-preferences.service';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.page.html',
  styleUrls: ['./settings.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule, NavbarComponent, TranslateModule]
})
export class SettingsPage implements OnInit, OnDestroy {
  isSaving = false;
  appVersion: string = '';
  languageDropdownOpen = false;
  currentUserId: string | null = null;
  private subscriptions: Subscription[] = [];

  notificationSettings: NotificationSettings = {
    criticalAlerts: true,
    accessRequests: true,
    sessionExpiry: true,
    requestUpdates: true,
    auditAlerts: false,
    maintenance: false,
    pushEnabled: true,
    emailEnabled: false
  };

  availableLanguages: Language[] = [];
  selectedLanguage: Language = { code: 'en', name: 'English (UK)' };

  constructor(
    private versionService: VersionService,
    private languageService: LanguageService,
    private userPreferencesService: UserPreferencesService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.loadNotificationSettings();
    this.loadLanguageSettings();
    this.appVersion = this.versionService.getVersion();

    // Debug: Test translation service
    console.log('Settings page initialized');
    console.log('Current language:', this.languageService.getCurrentLanguage());
    console.log('Available languages:', this.languageService.availableLanguages);
  }

  ngOnDestroy() {
    // Clean up subscriptions
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private loadNotificationSettings() {
    // Subscribe to auth state changes
    const authSub = this.authService.user$.subscribe(user => {
      if (user?.uid) {
        this.currentUserId = user.uid;
        this.loadUserPreferences();
      } else {
        this.currentUserId = null;
        // Load from localStorage as fallback
        this.loadFromLocalStorage();
      }
    });
    this.subscriptions.push(authSub);

    // Subscribe to user preferences changes
    const prefSub = this.userPreferencesService.userPreferences$.subscribe(preferences => {
      if (preferences) {
        this.notificationSettings = this.userPreferencesService.convertToNotificationSettings(preferences);
        console.log('✅ Notification settings updated from preferences:', this.notificationSettings);
      }
    });
    this.subscriptions.push(prefSub);
  }

  private loadUserPreferences() {
    if (!this.currentUserId) return;

    // The service will automatically load preferences when user changes
    // We just need to make sure we have the current preferences
    const currentPreferences = this.userPreferencesService.getCurrentPreferences();
    if (currentPreferences) {
      this.notificationSettings = this.userPreferencesService.convertToNotificationSettings(currentPreferences);
    }
  }

  private loadFromLocalStorage() {
    // Fallback to localStorage if user is not authenticated
    const savedSettings = localStorage.getItem('notificationSettings');
    if (savedSettings) {
      try {
        this.notificationSettings = { ...this.notificationSettings, ...JSON.parse(savedSettings) };
      } catch (error) {
        console.error('Error loading notification settings from localStorage:', error);
      }
    }
  }

  private loadLanguageSettings() {
    // Get available languages from the language service
    this.availableLanguages = this.languageService.availableLanguages;

    // Get current language from the language service
    this.selectedLanguage = this.languageService.getCurrentLanguageObject();

    // Subscribe to language changes
    this.languageService.currentLanguage$.subscribe(languageCode => {
      this.selectedLanguage = this.languageService.getCurrentLanguageObject();
    });
  }

  async saveNotificationSettings() {
    this.isSaving = true;

    try {
      if (this.currentUserId) {
        // Save to backend via API
        const userPreferences = this.userPreferencesService.convertFromNotificationSettings(
          this.notificationSettings,
          this.currentUserId
        );

        await this.userPreferencesService.updateUserPreferences(this.currentUserId, userPreferences).toPromise();
        console.log('✅ Notification settings saved to backend successfully');
      } else {
        // Fallback to localStorage if user is not authenticated
        localStorage.setItem('notificationSettings', JSON.stringify(this.notificationSettings));
        console.log('✅ Notification settings saved to localStorage');
      }

      // Request push permission if push notifications are enabled
      if (this.notificationSettings.pushEnabled && 'Notification' in window) {
        await this.requestNotificationPermission();
      }

    } catch (error) {
      console.error('❌ Error saving notification settings:', error);

      // Fallback to localStorage on API error
      try {
        localStorage.setItem('notificationSettings', JSON.stringify(this.notificationSettings));
        console.log('⚠️ Saved to localStorage as fallback');
      } catch (localError) {
        console.error('❌ Failed to save to localStorage as well:', localError);
      }
    } finally {
      this.isSaving = false;
    }
  }

  /**
   * Handle email notifications toggle change
   */
  onEmailNotificationsChange() {
    if (this.currentUserId) {
      // If email notifications are disabled, disable all email-related settings
      if (!this.notificationSettings.emailEnabled) {
        this.notificationSettings.requestUpdates = false;
      } else {
        // If email notifications are enabled, enable request updates by default
        this.notificationSettings.requestUpdates = true;
      }

      // Auto-save the change
      this.saveNotificationSettings();
    }
  }

  /**
   * Handle request updates toggle change
   */
  onRequestUpdatesChange() {
    if (this.currentUserId) {
      // Auto-save the change
      this.saveNotificationSettings();
    }
  }

  /**
   * Quick toggle for enabling all email notifications
   */
  async enableAllEmailNotifications() {
    if (!this.currentUserId) return;

    this.isSaving = true;
    try {
      await this.userPreferencesService.enableAllEmailNotifications(this.currentUserId).toPromise();
      console.log('✅ All email notifications enabled');
    } catch (error) {
      console.error('❌ Failed to enable all email notifications:', error);
    } finally {
      this.isSaving = false;
    }
  }

  /**
   * Quick toggle for disabling all email notifications
   */
  async disableAllEmailNotifications() {
    if (!this.currentUserId) return;

    this.isSaving = true;
    try {
      await this.userPreferencesService.disableAllEmailNotifications(this.currentUserId).toPromise();
      console.log('✅ All email notifications disabled');
    } catch (error) {
      console.error('❌ Failed to disable all email notifications:', error);
    } finally {
      this.isSaving = false;
    }
  }

  private async requestNotificationPermission() {
    try {
      if (Notification.permission === 'default') {
        const permission = await Notification.requestPermission();
        if (permission === 'granted') {
          console.log('Push notification permission granted');
          // You could show a test notification here
          this.showTestNotification();
        } else {
          console.log('Push notification permission denied');
          // Update the setting to reflect the denied permission
          this.notificationSettings.pushEnabled = false;
        }
      } else if (Notification.permission === 'granted') {
        this.showTestNotification();
      }
    } catch (error) {
      console.error('Error requesting notification permission:', error);
    }
  }

  private showTestNotification() {
    if (Notification.permission === 'granted') {
      new Notification('FireFighter Settings', {
        body: 'Push notifications are now enabled for emergency alerts.',
        icon: '/assets/icon/favicon.png',
        badge: '/assets/icon/favicon.png'
      });
    }
  }

  toggleLanguageDropdown() {
    this.languageDropdownOpen = !this.languageDropdownOpen;
  }

  selectLanguage(language: Language) {
    this.selectedLanguage = language;
    this.languageDropdownOpen = false;

    // Use the language service to switch languages
    this.languageService.setLanguage(language.code);

    console.log('Language switched to:', language.name, language.code);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    const dropdownButton = document.getElementById('language-dropdown-button');
    const dropdown = document.getElementById('language-dropdown');

    // Close dropdown if clicking outside of it
    if (this.languageDropdownOpen &&
        dropdownButton &&
        dropdown &&
        !dropdownButton.contains(target) &&
        !dropdown.contains(target)) {
      this.languageDropdownOpen = false;
    }
  }
}
