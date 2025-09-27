import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountPage } from './account.page';
import { testProviders, mockAuthService, mockLanguageService } from '../../../test-setup';
import { AuthService } from '../../services/auth.service';
import { LanguageService } from '../../services/language.service';

describe('AccountPage', () => {
  let component: AccountPage;
  let fixture: ComponentFixture<AccountPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountPage],
      providers: [
        ...testProviders,
        { provide: AuthService, useValue: mockAuthService },
        { provide: LanguageService, useValue: mockLanguageService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AccountPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
