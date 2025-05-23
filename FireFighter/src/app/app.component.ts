import { Component, OnInit } from '@angular/core';
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone';
import { AuthService } from './services/auth.service';
import { Capacitor } from '@capacitor/core';
import { FirebaseAuthentication } from '@capacitor-firebase/authentication';
import { initFlowbite } from 'flowbite';

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
}
