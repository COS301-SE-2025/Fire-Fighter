import { Component, OnInit } from '@angular/core';
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone';
import { AuthService } from './services/auth.service';
import { Capacitor } from '@capacitor/core';
import { FirebaseAuthentication } from '@capacitor-firebase/authentication';
import { initFlowbite } from 'flowbite';
import { StatusBar, Style } from '@capacitor/status-bar';
import { App } from '@capacitor/app';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  imports: [IonApp, IonRouterOutlet],
  standalone: true,
})
export class AppComponent implements OnInit {
  constructor(private authService: AuthService) {}

  ngOnInit() {
    // Initialize Firebase Authentication with Capacitor on mobile platforms
    if (Capacitor.isNativePlatform()) {
      this.initializeCapacitorAuth();
      this.setupStatusBar();
    }

    initFlowbite();
  }

  private async initializeCapacitorAuth() {
    // Listen for native auth state changes
    FirebaseAuthentication.addListener('authStateChange', async (change) => {
      if (change.user) {
        console.log('Native auth state changed: User is signed in');
        // You could emit this to your auth service if needed
      } else {
        console.log('Native auth state changed: User is signed out');
      }
    });
  }

  private async setupStatusBar() {
    try {
      if (Capacitor.getPlatform() === 'android') {
        // Hide the overlay and ensure status bar doesn't overlap content
        await StatusBar.setOverlaysWebView({ overlay: false });
        
        // Use light content (dark text) on light background
        await StatusBar.setStyle({ style: Style.Light });
        
        // Set background color to white
        await StatusBar.setBackgroundColor({ color: '#ffffff' });
        
        // Add listener for app state changes to ensure status bar settings persist
        App.addListener('appStateChange', ({ isActive }) => {
          if (isActive) {
            // Re-apply status bar settings when app becomes active
            StatusBar.setOverlaysWebView({ overlay: false });
            StatusBar.setStyle({ style: Style.Light });
            StatusBar.setBackgroundColor({ color: '#ffffff' });
          }
        });

        // Set a more appropriate value for status bar height
        // You can adjust this value (in pixels) to get the perfect spacing
        document.documentElement.style.setProperty(
          '--ion-safe-area-top', 
          '24px'
        );
      }
    } catch (error) {
      console.error('Error initializing status bar:', error);
    }
  }
}
