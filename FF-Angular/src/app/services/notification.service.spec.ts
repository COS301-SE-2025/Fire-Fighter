import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NotificationService, Notification } from './notification.service';
import { AuthService } from './auth.service';
import { of } from 'rxjs';

describe('NotificationService', () => {
  let service: NotificationService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockUser = { uid: 'test-user-123' };
  const mockNotifications: Notification[] = [
    {
      id: 1,
      userId: 'test-user-123',
      type: 'ticket_created',
      title: 'Test Notification',
      message: 'Test message',
      timestamp: new Date(),
      read: false,
      ticketId: 'TEST-001'
    }
  ];

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['user$']);
    authSpy.user$ = of(mockUser);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        NotificationService,
        { provide: AuthService, useValue: authSpy }
      ]
    });

    service = TestBed.inject(NotificationService);
    httpMock = TestBed.inject(HttpTestingController);
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
    service.stopPolling();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should load notifications on initialization', () => {
    const req = httpMock.expectOne(`http://localhost:8080/api/notifications?userId=${mockUser.uid}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockNotifications);

    service.getNotifications().subscribe(notifications => {
      expect(notifications.length).toBe(1);
      expect(notifications[0].title).toBe('Test Notification');
    });
  });

  it('should force refresh notifications', () => {
    // Initial load
    const initialReq = httpMock.expectOne(`http://localhost:8080/api/notifications?userId=${mockUser.uid}`);
    initialReq.flush(mockNotifications);

    // Force refresh
    service.forceRefresh();

    const refreshReq = httpMock.expectOne(`http://localhost:8080/api/notifications?userId=${mockUser.uid}`);
    expect(refreshReq.request.method).toBe('GET');
    refreshReq.flush(mockNotifications);
  });

  it('should mark notification as read', () => {
    // Initial load
    const initialReq = httpMock.expectOne(`http://localhost:8080/api/notifications?userId=${mockUser.uid}`);
    initialReq.flush(mockNotifications);

    service.markAsRead(1);

    const markReadReq = httpMock.expectOne(`http://localhost:8080/api/notifications/1/read?userId=${mockUser.uid}`);
    expect(markReadReq.request.method).toBe('PUT');
    markReadReq.flush({});
  });

  it('should handle errors gracefully', () => {
    const req = httpMock.expectOne(`http://localhost:8080/api/notifications?userId=${mockUser.uid}`);
    req.error(new ErrorEvent('Network error'));

    service.getNotifications().subscribe(notifications => {
      expect(notifications).toEqual([]);
    });
  });
});
