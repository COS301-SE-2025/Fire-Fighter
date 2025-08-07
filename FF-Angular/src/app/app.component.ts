import { Component, OnInit } from '@angular/core';
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone';
import { AuthService } from './services/auth.service';
import { HealthService } from './services/health.service';
import { HealthMonitorService } from './services/health-monitor.service';
import { AppLoadingService } from './services/app-loading.service';
import { LanguageService } from './services/language.service';
import { AppLoadingScreenComponent } from './components/app-loading-screen/app-loading-screen.component';
import { Capacitor } from '@capacitor/core';
import { FirebaseAuthentication } from '@capacitor-firebase/authentication';
import { initFlowbite } from 'flowbite';
import { StatusBar, Style } from '@capacitor/status-bar';
import { App } from '@capacitor/app';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  imports: [IonApp, IonRouterOutlet, AppLoadingScreenComponent],
  standalone: true,
})
export class AppComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private healthService: HealthService,
    private healthMonitorService: HealthMonitorService,
    private appLoadingService: AppLoadingService,
    private languageService: LanguageService
  ) {}

  ngOnInit() {
    console.log('=== APP INITIALIZATION DEBUG ===');
    console.log('1. App component initialized');
    console.log('2. Platform:', Capacitor.getPlatform());
    console.log('3. Is native platform:', Capacitor.isNativePlatform());

    // Initialize platform-specific features first
    if (Capacitor.isNativePlatform()) {
      this.initializeCapacitorAuth();
      this.setupStatusBar();
    }

    // Initialize FlowBite
    initFlowbite();

    // Start the coordinated app initialization process first
    console.log('4. Starting app loading sequence...');
    this.appLoadingService.initializeApp().then(() => {
      console.log('✅ App initialization complete');

      // After app loading is complete, start health monitoring
      console.log('5. Starting health monitoring...');
      this.healthService.startMonitoring();
      this.healthMonitorService.startMonitoring();

    }).catch((error: any) => {
      console.error('❌ App initialization failed:', error);

      // Even if initialization fails, start health monitoring
      // This ensures the app can still function with limited connectivity
      console.log('6. Starting health monitoring (fallback mode)...');
      this.healthService.startMonitoring();
      this.healthMonitorService.startMonitoring();
    });

    console.log('=== END APP INITIALIZATION DEBUG ===');
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
