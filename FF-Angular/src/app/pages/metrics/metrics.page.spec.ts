import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MetricsPage } from './metrics.page';
import { testProviders, mockAuthService, mockTicketService, mockNotificationService } from '../../../test-setup';
import { AuthService } from '../../services/auth.service';
import { TicketService } from '../../services/ticket.service';
import { NotificationService } from '../../services/notification.service';

describe('MetricsPage', () => {
  let component: MetricsPage;
  let fixture: ComponentFixture<MetricsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetricsPage],
      providers: [
        ...testProviders,
        { provide: AuthService, useValue: mockAuthService },
        { provide: TicketService, useValue: mockTicketService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MetricsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
