import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { VersionService } from '../../services/version.service';

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

interface Language {
  code: string;
  name: string;
}

@Component({
  selector: 'app-settings',
  templateUrl: './settings.page.html',
  styleUrls: ['./settings.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule, NavbarComponent]
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

  availableLanguages: Language[] = [
    {
      code: 'en-GB',
      name: 'English (UK)'
    },
    {
      code: 'de-DE',
      name: 'Deutsch (German)'
    }
  ];

  selectedLanguage: Language = this.availableLanguages[0]; // Default to English (UK)

  constructor(private versionService: VersionService) { }

  ngOnInit() {
    this.loadNotificationSettings();
    this.loadLanguageSettings();
    this.appVersion = this.versionService.getVersion();
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
    // Load language settings from localStorage
    const savedLanguage = localStorage.getItem('selectedLanguage');
    if (savedLanguage) {
      try {
        const languageCode = JSON.parse(savedLanguage);
        const foundLanguage = this.availableLanguages.find(lang => lang.code === languageCode);
        if (foundLanguage) {
          this.selectedLanguage = foundLanguage;
        }
      } catch (error) {
        console.error('Error loading language settings:', error);
      }
    }
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

    // Save language preference to localStorage
    localStorage.setItem('selectedLanguage', JSON.stringify(language.code));

    // Log the selection for now (in a real implementation, this would trigger language change)
    console.log('Language selected:', language.name, language.code);

    // TODO: Implement actual language switching functionality
    // This could involve:
    // - Loading different translation files
    // - Updating Angular i18n locale
    // - Refreshing the application with new language
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
