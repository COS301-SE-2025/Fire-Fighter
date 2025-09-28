import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardPage } from './dashboard.page';
import { testProviders, mockAuthService, mockTicketService, mockNotificationService } from '../../../test-setup';
import { AuthService } from '../../services/auth.service';
import { TicketService } from '../../services/ticket.service';
import { NotificationService } from '../../services/notification.service';

describe('DashboardPage', () => {
  let component: DashboardPage;
  let fixture: ComponentFixture<DashboardPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardPage],
      providers: [
        ...testProviders,
        { provide: AuthService, useValue: mockAuthService },
        { provide: TicketService, useValue: mockTicketService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
