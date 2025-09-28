import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app.component';
import {
  testProviders,
  mockAuthService,
  mockHealthService,
  mockLanguageService,
  mockHealthMonitorService,
  mockAppLoadingService,
  mockTokenMonitoringService
} from '../test-setup';
import { AuthService } from './services/auth.service';
import { HealthService } from './services/health.service';
import { LanguageService } from './services/language.service';
import { HealthMonitorService } from './services/health-monitor.service';
import { AppLoadingService } from './services/app-loading.service';
import { TokenMonitoringService } from './services/token-monitoring.service';

describe('AppComponent', () => {
  it('should create the app', async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        ...testProviders,
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService },
        { provide: HealthService, useValue: mockHealthService },
        { provide: LanguageService, useValue: mockLanguageService },
        { provide: HealthMonitorService, useValue: mockHealthMonitorService },
        { provide: AppLoadingService, useValue: mockAppLoadingService },
        { provide: TokenMonitoringService, useValue: mockTokenMonitoringService }
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
