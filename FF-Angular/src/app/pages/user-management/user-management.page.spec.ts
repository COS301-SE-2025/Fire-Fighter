import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserManagementPage } from './user-management.page';
import { testProviders, mockAuthService, mockLanguageService } from '../../../test-setup';
import { AuthService } from '../../services/auth.service';
import { LanguageService } from '../../services/language.service';

describe('UserManagementPage', () => {
  let component: UserManagementPage;
  let fixture: ComponentFixture<UserManagementPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserManagementPage],
      providers: [
        ...testProviders,
        { provide: AuthService, useValue: mockAuthService },
        { provide: LanguageService, useValue: mockLanguageService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserManagementPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
