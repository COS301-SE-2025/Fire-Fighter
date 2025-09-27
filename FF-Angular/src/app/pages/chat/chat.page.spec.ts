import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChatPage } from './chat.page';
import { testProviders, mockAuthService, mockChatbotService, mockApiConfigService } from '../../../test-setup';
import { AuthService } from '../../services/auth.service';
import { ChatbotService } from '../../services/chatbot.service';
import { ApiConfigService } from '../../services/api-config.service';

describe('ChatPage', () => {
  let component: ChatPage;
  let fixture: ComponentFixture<ChatPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatPage],
      providers: [
        ...testProviders,
        { provide: AuthService, useValue: mockAuthService },
        { provide: ChatbotService, useValue: mockChatbotService },
        { provide: ApiConfigService, useValue: mockApiConfigService }
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
