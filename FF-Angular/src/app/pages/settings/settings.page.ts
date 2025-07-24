import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { VersionService } from '../../services/version.service';
import { LanguageService, Language } from '../../services/language.service';
import { TranslateModule } from '@ngx-translate/core';

interface NotificationSettings {
  criticalAlerts: boolean;
  accessRequests: boolean;
  sessionExpiry: boolean;
  requestUpdates: boolean;
  auditAlerts: boolean;
  maintenance: boolean;
  pushEnabled: boolean;
  emailEnabled: boolean;
}



@Component({
  selector: 'app-settings',
  templateUrl: './settings.page.html',
  styleUrls: ['./settings.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule, NavbarComponent, TranslateModule]
})
export class SettingsPage implements OnInit {

  isSaving = false;
  appVersion: string = '';
  languageDropdownOpen = false;

  notificationSettings: NotificationSettings = {
    criticalAlerts: true,
    accessRequests: true,
    sessionExpiry: true,
    requestUpdates: true,
    auditAlerts: false,
    maintenance: false,
    pushEnabled: true,
    emailEnabled: true
  };

  availableLanguages: Language[] = [];
  selectedLanguage: Language = { code: 'en', name: 'English (UK)' };

  constructor(
    private versionService: VersionService,
    private languageService: LanguageService
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

  private loadNotificationSettings() {
    // Load settings from localStorage or service
    const savedSettings = localStorage.getItem('notificationSettings');
    if (savedSettings) {
      try {
        this.notificationSettings = { ...this.notificationSettings, ...JSON.parse(savedSettings) };
      } catch (error) {
        console.error('Error loading notification settings:', error);
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
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Save to localStorage (in a real app, this would be an API call)
      localStorage.setItem('notificationSettings', JSON.stringify(this.notificationSettings));
      
      // Show success message (you could add a toast notification here)
      console.log('Notification settings saved successfully');
      
      // Request push permission if push notifications are enabled
      if (this.notificationSettings.pushEnabled && 'Notification' in window) {
        await this.requestNotificationPermission();
      }
      
    } catch (error) {
      console.error('Error saving notification settings:', error);
      // Handle error (show error message to user)
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
