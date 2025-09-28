import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RequestsPage } from './requests.page';
import { testProviders, mockTicketService, mockAuthService, mockLanguageService } from '../../../test-setup';
import { TicketService } from '../../services/ticket.service';
import { AuthService } from '../../services/auth.service';
import { LanguageService } from '../../services/language.service';
import { ToastController } from '@ionic/angular';

describe('RequestsPage', () => {
  let component: RequestsPage;
  let fixture: ComponentFixture<RequestsPage>;

  const mockToastController = {
    create: jasmine.createSpy('create').and.returnValue(Promise.resolve({
      present: jasmine.createSpy('present')
    }))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestsPage],
      providers: [
        ...testProviders,
        { provide: TicketService, useValue: mockTicketService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: LanguageService, useValue: mockLanguageService },
        { provide: ToastController, useValue: mockToastController }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RequestsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
