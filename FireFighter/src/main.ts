// src/main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import {
  RouteReuseStrategy,
  provideRouter,
  withPreloading,
  PreloadAllModules
} from '@angular/router';
import {
  IonicRouteStrategy,
  provideIonicAngular
} from '@ionic/angular/standalone';

import { routes }       from './app/app.routes';
import { AppComponent } from './app/app.component';

// ← THESE imports are from @angular/fire
import { provideFirebaseApp, initializeApp } from '@angular/fire/app';
import { provideAuth,           getAuth      } from '@angular/fire/auth';

// (optional) if you want analytics
import { getAnalytics } from 'firebase/analytics';

// ——— Your Firebase config ———
const firebaseConfig = {
  apiKey: "AIzaSyDAIQCimq3CIx07N7WrsmXK3WzPpi0WeRc",
  authDomain: "bwm-it-hub-firefighter.firebaseapp.com",
  projectId: "bwm-it-hub-firefighter",
  storageBucket: "bwm-it-hub-firefighter.firebasestorage.app",
  messagingSenderId: "375672884424",
  appId: "1:375672884424:web:226306eb10e08f9004e9fd",
  measurementId: "G-Z5KSBXQN29"
};

// Initialize once
const firebaseApp  = initializeApp(firebaseConfig);
const firebaseAuth = getAuth(firebaseApp);
// (optional) const analytics = getAnalytics(firebaseApp);

bootstrapApplication(AppComponent, {
  providers: [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    provideIonicAngular(),
    provideRouter(routes, withPreloading(PreloadAllModules)),

    // ← wire AngularFire into Angular’s DI
    provideFirebaseApp(() => firebaseApp),
    provideAuth      (() => firebaseAuth),

    // (optional) if you install @angular/fire/analytics:
    // provideAnalytics(() => analytics),
  ]
});