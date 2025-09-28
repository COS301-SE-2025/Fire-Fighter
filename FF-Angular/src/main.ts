/// <reference types="@angular/localize" />

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
import { provideHttpClient, withInterceptors, HttpErrorResponse, HttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

// Translation imports
import { importProvidersFrom } from '@angular/core';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

// Translation loader factory
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

import { routes }       from './app/app.routes';
import { AppComponent } from './app/app.component';
import { ApiConfigService } from './app/services/api-config.service';
import { environment } from './environments/environment';
import { jwtInterceptor } from './app/interceptors/jwt.interceptor';
import { authErrorInterceptor } from './app/interceptors/auth-error.interceptor';


// ← THESE imports are from @angular/fire
import { provideFirebaseApp, initializeApp } from '@angular/fire/app';
import { provideAuth,           getAuth      } from '@angular/fire/auth';

// (optional) if you want analytics
import { getAnalytics } from 'firebase/analytics';

// ——— Your Firebase config ———
const firebaseConfig = {
  apiKey: "AIzaSyBl9MRD2KQCgrZ0QC_GAsz1-JBJloBt5O8", // Updated to match google-services.json
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
    provideHttpClient(withInterceptors([
      jwtInterceptor, // Add JWT token to all API requests and handle refresh
      authErrorInterceptor, // Handle 401 errors and automatic token refresh
      (req, next) => {
        const apiConfigService = inject(ApiConfigService);
        const currentApiUrl = apiConfigService.getCurrentApiUrlSync();

        // Update request URL to use current API URL
        let modifiedRequest = req;
        if (req.url.includes(environment.apiUrl)) {
          const updatedUrl = req.url.replace(environment.apiUrl, currentApiUrl);
          modifiedRequest = req.clone({ url: updatedUrl });
        }

        return next(modifiedRequest).pipe(
          catchError((error: HttpErrorResponse) => {
            console.log('🔍 HTTP Error detected:', {
              url: modifiedRequest.url,
              status: error.status,
              message: error.message,
              error: error.error,
              name: error.name,
              isProgressEvent: error.error instanceof ProgressEvent
            });

            return throwError(() => error);
          })
        );
      }
    ])),
    provideAnimations(),

    // Translation configuration
    importProvidersFrom(
      TranslateModule.forRoot({
        loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient]
        },
        defaultLanguage: 'en'
      })
    ),

    // ← wire AngularFire into Angular’s DI
    provideFirebaseApp(() => firebaseApp),
    provideAuth      (() => firebaseAuth),

    // (optional) if you install @angular/fire/analytics:
    // provideAnalytics(() => analytics),
  ]
});
