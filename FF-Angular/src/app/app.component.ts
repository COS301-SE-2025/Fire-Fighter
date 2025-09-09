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
    if (Capacitor.isNativePlatform()) {
      this.initializeCapacitorAuth();
      this.setupStatusBar();
    }

    initFlowbite();
    
    this.appLoadingService.setLoadingTimeout(30000);
    
    this.appLoadingService.initializeApp().then(() => {
      this.healthService.startMonitoring();
      this.healthMonitorService.startMonitoring();
    }).catch(() => {
      this.healthService.startMonitoring();
      this.healthMonitorService.startMonitoring();
    });
  }

  private async initializeCapacitorAuth() {
    FirebaseAuthentication.addListener('authStateChange', async (change) => {
      // Handle auth state changes if needed
    });
  }

  private async setupStatusBar() {
    try {
      if (Capacitor.getPlatform() === 'android') {
        await StatusBar.setOverlaysWebView({ overlay: false });
        await StatusBar.setStyle({ style: Style.Light });
        await StatusBar.setBackgroundColor({ color: '#ffffff' });
        
        App.addListener('appStateChange', ({ isActive }) => {
          if (isActive) {
            StatusBar.setOverlaysWebView({ overlay: false });
            StatusBar.setStyle({ style: Style.Light });
            StatusBar.setBackgroundColor({ color: '#ffffff' });
          }
        });

        document.documentElement.style.setProperty('--ion-safe-area-top', '24px');
      }
    } catch (error) {
      // Status bar setup failed
    }
  }
}
