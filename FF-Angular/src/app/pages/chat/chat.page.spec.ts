import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChatPage } from './chat.page';
import { testProviders, mockAuthService } from '../../../test-setup';
import { AuthService } from '../../services/auth.service';

describe('ChatPage', () => {
  let component: ChatPage;
  let fixture: ComponentFixture<ChatPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatPage],
      providers: [
        ...testProviders,
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ChatPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
