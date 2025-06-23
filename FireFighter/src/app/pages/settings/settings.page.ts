import { Component, OnInit } from '@angular/core';
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

  constructor(private versionService: VersionService) { }

  ngOnInit() {
    this.loadNotificationSettings();
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
}
